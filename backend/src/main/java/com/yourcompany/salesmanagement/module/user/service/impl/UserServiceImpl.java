package com.yourcompany.salesmanagement.module.user.service.impl;

import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.common.security.SecurityUtils;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.user.dto.request.AssignRolesRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.ChangeMyPasswordRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.CreateUserRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.ResetPasswordRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.UpdateUserRequest;
import com.yourcompany.salesmanagement.module.user.dto.response.UserDetailResponse;
import com.yourcompany.salesmanagement.module.user.dto.response.UserResponse;
import com.yourcompany.salesmanagement.module.user.entity.Role;
import com.yourcompany.salesmanagement.module.user.entity.User;
import com.yourcompany.salesmanagement.module.user.repository.RoleRepository;
import com.yourcompany.salesmanagement.module.user.repository.UserRepository;
import com.yourcompany.salesmanagement.module.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Override
    public UserDetailResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
        return toUserDetailResponse(user);
    }

    @Override
    @Transactional
    public UserDetailResponse createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new BusinessException("Username already exists", HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setUsername(request.username());
        user.setEmail(blankToNull(request.email()));
        user.setPhone(blankToNull(request.phone()));
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus("ACTIVE");

        if (request.roleCodes() != null && !request.roleCodes().isEmpty()) {
            Set<Role> roles = loadRolesByCodes(request.roleCodes());
            user.getRoles().addAll(roles);
        }

        user = userRepository.save(user);
        return toUserDetailResponse(user);
    }

    @Override
    @Transactional
    public UserDetailResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        user.setFullName(request.fullName());
        user.setEmail(blankToNull(request.email()));
        user.setPhone(blankToNull(request.phone()));
        if (request.status() != null && !request.status().isBlank()) {
            user.setStatus(request.status().trim());
        }
        user = userRepository.save(user);
        return toUserDetailResponse(user);
    }

    @Override
    @Transactional
    public UserDetailResponse assignRoles(Long userId, AssignRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        Set<Role> roles = loadRolesByCodes(request.roleCodes());
        user.getRoles().clear();
        user.getRoles().addAll(roles);
        user = userRepository.save(user);
        return toUserDetailResponse(user);
    }

    @Override
    @Transactional
    public void changeMyPassword(ChangeMyPasswordRequest request) {
        UserPrincipal principal = SecurityUtils.requirePrincipal();
        User user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BusinessException("Current password is incorrect", HttpStatus.BAD_REQUEST);
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new BusinessException("New password must be different from current password", HttpStatus.BAD_REQUEST);
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void adminResetPassword(Long userId, ResetPasswordRequest request) {
        UserPrincipal principal = SecurityUtils.requirePrincipal();
        if (!isAdminLike(principal)) {
            throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private boolean isAdminLike(UserPrincipal principal) {
        if (principal == null || principal.roleCodes() == null) return false;
        return principal.roleCodes().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .anyMatch(code -> code.equalsIgnoreCase("STORE_MANAGER") || code.equalsIgnoreCase("ADMIN") || code.equalsIgnoreCase("SUPER_ADMIN"));
    }

    private Set<Role> loadRolesByCodes(List<String> roleCodes) {
        var normalized = roleCodes.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());

        if (normalized.isEmpty()) {
            throw new BusinessException("roleCodes must not be empty", HttpStatus.BAD_REQUEST);
        }

        var roles = roleRepository.findAll().stream()
                .filter(r -> normalized.contains(r.getCode()))
                .collect(Collectors.toSet());

        if (roles.size() != normalized.size()) {
            throw new BusinessException("Some roles were not found", HttpStatus.BAD_REQUEST);
        }
        return roles;
    }

    private UserResponse toUserResponse(User user) {
        List<String> roleCodes = user.getRoles().stream().map(Role::getCode).distinct().toList();
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getStatus(), roleCodes);
    }

    private UserDetailResponse toUserDetailResponse(User user) {
        List<String> roleCodes = user.getRoles().stream().map(Role::getCode).distinct().toList();
        return new UserDetailResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                roleCodes
        );
    }

    private String blankToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
