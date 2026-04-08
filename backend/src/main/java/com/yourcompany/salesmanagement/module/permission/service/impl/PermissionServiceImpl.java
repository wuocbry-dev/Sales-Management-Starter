package com.yourcompany.salesmanagement.module.permission.service.impl;

import com.yourcompany.salesmanagement.module.permission.dto.response.PermissionResponse;
import com.yourcompany.salesmanagement.module.permission.repository.PermissionRepository;
import com.yourcompany.salesmanagement.module.permission.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<PermissionResponse> getPermissions() {
        return permissionRepository.findAll().stream()
                .map(p -> new PermissionResponse(p.getId(), p.getName(), p.getCode(), p.getModuleName(), p.getDescription()))
                .toList();
    }
}

