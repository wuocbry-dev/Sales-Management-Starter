package com.yourcompany.salesmanagement.module.auth.service;

import com.yourcompany.salesmanagement.module.auth.dto.request.LoginRequest;
import com.yourcompany.salesmanagement.module.auth.dto.request.RegisterRequest;
import com.yourcompany.salesmanagement.module.auth.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);
}
