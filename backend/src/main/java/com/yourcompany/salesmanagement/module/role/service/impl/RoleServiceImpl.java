package com.yourcompany.salesmanagement.module.role.service.impl;

import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.common.audit.AuditLoggable;
import com.yourcompany.salesmanagement.module.permission.repository.PermissionRepository;
import com.yourcompany.salesmanagement.module.role.dto.request.CreateRoleRequest;
import com.yourcompany.salesmanagement.module.role.dto.response.RoleResponse;
import com.yourcompany.salesmanagement.module.role.service.RoleService;
import com.yourcompany.salesmanagement.module.user.entity.Role;
import com.yourcompany.salesmanagement.module.user.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<RoleResponse> getRoles() {
        return roleRepository.findAll().stream()
                .map(r -> new RoleResponse(
                        r.getId(),
                        r.getName(),
                        r.getCode(),
                        r.getDescription(),
                        r.getStatus(),
                        r.getPermissions().stream().map(p -> p.getCode()).distinct().toList()
                ))
                .toList();
    }

    @Override
    @Transactional
    @AuditLoggable(module = "role", action = "CREATE", entityType = "Role")
    public RoleResponse create(CreateRoleRequest request) {
        String code = request.code().trim();
        if (roleRepository.findByCode(code).isPresent()) {
            throw new BusinessException("Role code already exists", HttpStatus.CONFLICT);
        }

        var normalizedPerms = request.permissionCodes().stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .collect(Collectors.toSet());
        if (normalizedPerms.isEmpty()) {
            throw new BusinessException("permissionCodes must not be empty", HttpStatus.BAD_REQUEST);
        }

        var permissions = permissionRepository.findAllByCodeIn(List.copyOf(normalizedPerms));
        Set<String> foundCodes = permissions.stream().map(p -> p.getCode()).collect(Collectors.toSet());
        if (foundCodes.size() != normalizedPerms.size()) {
            throw new BusinessException("Some permissions were not found", HttpStatus.BAD_REQUEST);
        }

        Role role = new Role();
        role.setName(request.name().trim());
        role.setCode(code);
        role.setDescription(request.description());
        role.setIsSystem(false);
        role.setStatus("ACTIVE");
        role.getPermissions().addAll(permissions);
        role = roleRepository.save(role);

        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getCode(),
                role.getDescription(),
                role.getStatus(),
                role.getPermissions().stream().map(p -> p.getCode()).distinct().toList()
        );
    }
}

