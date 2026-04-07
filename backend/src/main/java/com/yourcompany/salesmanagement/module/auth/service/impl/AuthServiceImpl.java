package com.yourcompany.salesmanagement.module.auth.service.impl;

import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.dto.request.LoginRequest;
import com.yourcompany.salesmanagement.module.auth.dto.response.LoginResponse;
import com.yourcompany.salesmanagement.module.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public LoginResponse login(LoginRequest request) {
        if ("admin".equals(request.username()) && "admin123".equals(request.password())) {
            return new LoginResponse(
                    "demo-token-admin",
                    "Bearer",
                    "admin",
                    "ADMIN",
                    "System Administrator"
            );
        }

        if ("manager".equals(request.username()) && "manager123".equals(request.password())) {
            return new LoginResponse(
                    "demo-token-manager",
                    "Bearer",
                    "manager",
                    "MANAGER",
                    "Store Manager"
            );
        }

        throw new BusinessException("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }
}
