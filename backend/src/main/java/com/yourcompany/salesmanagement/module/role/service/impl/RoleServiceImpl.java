package com.yourcompany.salesmanagement.module.role.service.impl;

import com.yourcompany.salesmanagement.module.role.dto.response.RoleResponse;
import com.yourcompany.salesmanagement.module.role.service.RoleService;
import com.yourcompany.salesmanagement.module.user.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
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
}

