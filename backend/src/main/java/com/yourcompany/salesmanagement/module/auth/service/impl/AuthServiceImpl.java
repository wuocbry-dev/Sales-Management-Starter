package com.yourcompany.salesmanagement.module.auth.service.impl;

import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.dto.request.LoginRequest;
import com.yourcompany.salesmanagement.module.auth.dto.request.RegisterRequest;
import com.yourcompany.salesmanagement.module.auth.dto.response.LoginResponse;
import com.yourcompany.salesmanagement.module.auth.service.AuthContextService;
import com.yourcompany.salesmanagement.module.auth.service.AuthService;
import com.yourcompany.salesmanagement.module.auth.service.JwtService;
import com.yourcompany.salesmanagement.module.branch.entity.Branch;
import com.yourcompany.salesmanagement.module.branch.repository.BranchRepository;
import com.yourcompany.salesmanagement.module.store.entity.Store;
import com.yourcompany.salesmanagement.module.store.repository.StoreRepository;
import com.yourcompany.salesmanagement.module.user.entity.Role;
import com.yourcompany.salesmanagement.module.user.entity.User;
import com.yourcompany.salesmanagement.module.user.repository.RoleRepository;
import com.yourcompany.salesmanagement.module.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Objects;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthContextService authContextService;
    private final StoreRepository storeRepository;
    private final BranchRepository branchRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthContextService authContextService,
                           StoreRepository storeRepository,
                           BranchRepository branchRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authContextService = authContextService;
        this.storeRepository = storeRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Invalid username or password", HttpStatus.UNAUTHORIZED));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new BusinessException("User is not active", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        List<String> roleCodes = user.getRoles().stream()
                .map(r -> r.getCode())
                .distinct()
                .toList();

        List<String> permissionCodes = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getCode())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        var ctx = authContextService.resolveForUserId(user.getId());
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), roleCodes, permissionCodes, ctx.storeId(), ctx.branchId());
        String primaryRole = roleCodes.isEmpty() ? null : roleCodes.get(0);

        return new LoginResponse(
                accessToken,
                "Bearer",
                user.getUsername(),
                primaryRole,
                user.getFullName()
        );
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new BusinessException("Username already exists", HttpStatus.CONFLICT);
        }

        var defaultRole = roleRepository.findByCode("STORE_MANAGER")
                .orElseGet(this::createDefaultStoreManagerRole);

        User user = new User();
        user.setFullName(request.fullName());
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus("ACTIVE");
        user.getRoles().add(defaultRole);
        user = userRepository.save(user);

        Store store = new Store();
        store.setName(request.storeName());
        store.setCode(generateStoreCode(request.storeName()));
        store.setBusinessType(request.businessType());
        store.setOwnerUserId(user.getId());
        store = storeRepository.save(store);

        Branch branch = new Branch();
        branch.setStoreId(store.getId());
        branch.setName("Chi nhanh mac dinh");
        branch.setCode("BRANCH_DEFAULT_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        branch.setIsDefault(true);
        branch.setStatus("ACTIVE");
        branchRepository.save(branch);

        List<String> roleCodes = user.getRoles().stream().map(r -> r.getCode()).distinct().toList();
        List<String> permissionCodes = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getCode())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();
        var ctx = authContextService.resolveForUserId(user.getId());
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getUsername(), roleCodes, permissionCodes, ctx.storeId(), ctx.branchId());
        String primaryRole = roleCodes.isEmpty() ? null : roleCodes.get(0);

        return new LoginResponse(accessToken, "Bearer", user.getUsername(), primaryRole, user.getFullName());
    }

    private Role createDefaultStoreManagerRole() {
        Role role = new Role();
        role.setName("Store Manager");
        role.setCode("STORE_MANAGER");
        role.setDescription("Default role for store owner/manager");
        role.setIsSystem(true);
        role.setStatus("ACTIVE");
        return roleRepository.save(role);
    }

    private String generateStoreCode(String storeName) {
        String normalized = storeName == null ? "STORE" : storeName.trim().toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_");
        if (normalized.isBlank()) normalized = "STORE";
        String suffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
        String code = (normalized.length() > 30 ? normalized.substring(0, 30) : normalized) + "_" + suffix;
        if (code.length() > 50) {
            code = code.substring(0, 50);
        }
        return code;
    }
}
