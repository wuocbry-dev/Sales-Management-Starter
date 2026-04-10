package com.yourcompany.salesmanagement.module.user.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.user.dto.request.AssignRolesRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.ChangeMyPasswordRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.CreateUserRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.ResetPasswordRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.UpdateUserRequest;
import com.yourcompany.salesmanagement.module.user.dto.response.UserDetailResponse;
import com.yourcompany.salesmanagement.module.user.dto.response.UserResponse;
import com.yourcompany.salesmanagement.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/users", "/api/users"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ') or hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    public BaseResponse<List<UserResponse>> getUsers() {
        return BaseResponse.ok("Users fetched successfully", userService.getUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_READ') or hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    public BaseResponse<UserDetailResponse> getUserById(@PathVariable Long id) {
        return BaseResponse.ok("User fetched successfully", userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_WRITE') or hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    public BaseResponse<UserDetailResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return BaseResponse.ok("User created successfully", userService.createUser(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE') or hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    public BaseResponse<UserDetailResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return BaseResponse.ok("User updated successfully", userService.updateUser(id, request));
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('USER_WRITE') or hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    public BaseResponse<UserDetailResponse> assignRolesPost(@PathVariable Long id, @Valid @RequestBody AssignRolesRequest request) {
        return BaseResponse.ok("Roles assigned successfully", userService.assignRoles(id, request));
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('USER_WRITE') or hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    public BaseResponse<UserDetailResponse> assignRoles(@PathVariable Long id, @Valid @RequestBody AssignRolesRequest request) {
        return BaseResponse.ok("Roles assigned successfully", userService.assignRoles(id, request));
    }

    @PutMapping("/me/password")
    public BaseResponse<Void> changeMyPassword(@Valid @RequestBody ChangeMyPasswordRequest request) {
        userService.changeMyPassword(request);
        return BaseResponse.ok("Password changed successfully", null);
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasAuthority('USER_WRITE') or hasAnyRole('ADMIN','SYSTEM_ADMIN')")
    public BaseResponse<Void> adminResetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        userService.adminResetPassword(id, request);
        return BaseResponse.ok("Password reset successfully", null);
    }
}
