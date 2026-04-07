package com.yourcompany.salesmanagement.module.user.service.impl;

import com.yourcompany.salesmanagement.module.user.dto.response.UserResponse;
import com.yourcompany.salesmanagement.module.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public List<UserResponse> getUsers() {
        return List.of(
                new UserResponse(1L, "admin", "System Administrator", "ADMIN", "ACTIVE"),
                new UserResponse(2L, "manager", "Store Manager", "MANAGER", "ACTIVE"),
                new UserResponse(3L, "cashier01", "Cashier 01", "CASHIER", "ACTIVE")
        );
    }
}
