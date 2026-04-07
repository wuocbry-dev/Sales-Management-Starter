package com.yourcompany.salesmanagement.module.auth.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.auth.dto.request.LoginRequest;
import com.yourcompany.salesmanagement.module.auth.dto.request.RegisterRequest;
import com.yourcompany.salesmanagement.module.auth.dto.response.LoginResponse;
import com.yourcompany.salesmanagement.module.auth.dto.response.MeResponse;
import com.yourcompany.salesmanagement.module.auth.service.AuthService;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.yourcompany.salesmanagement.module.user.repository.UserRepository;

@RestController
@RequestMapping({"/api/v1/auth", "/api/auth"})
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return BaseResponse.ok("Login successful", authService.login(request));
    }

    @PostMapping("/register")
    public BaseResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return BaseResponse.ok("Register successful", authService.register(request));
    }

    @GetMapping("/me")
    public BaseResponse<MeResponse> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return BaseResponse.fail("Unauthenticated", null);
        }

        String fullName = userRepository.findById(principal.userId()).map(u -> u.getFullName()).orElse(null);
        return BaseResponse.ok("OK", new MeResponse(
                principal.userId(),
                principal.username(),
                fullName,
                principal.roleCodes(),
                principal.storeId(),
                principal.branchId()
        ));
    }
}
