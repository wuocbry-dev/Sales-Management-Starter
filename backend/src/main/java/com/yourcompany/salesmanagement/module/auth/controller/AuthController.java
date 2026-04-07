package com.yourcompany.salesmanagement.module.auth.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.auth.dto.request.LoginRequest;
import com.yourcompany.salesmanagement.module.auth.dto.response.LoginResponse;
import com.yourcompany.salesmanagement.module.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return BaseResponse.ok("Login successful", authService.login(request));
    }
}
