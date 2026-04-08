package com.yourcompany.salesmanagement.module.user.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.user.dto.request.AssignRolesRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.CreateUserRequest;
import com.yourcompany.salesmanagement.module.user.dto.response.UserDetailResponse;
import com.yourcompany.salesmanagement.module.user.dto.response.UserResponse;
import com.yourcompany.salesmanagement.module.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public BaseResponse<List<UserResponse>> getUsers() {
        return BaseResponse.ok("Users fetched successfully", userService.getUsers());
    }

    @GetMapping("/{id}")
    public BaseResponse<UserDetailResponse> getUserById(@PathVariable Long id) {
        return BaseResponse.ok("User fetched successfully", userService.getUserById(id));
    }

    @PostMapping
    public BaseResponse<UserDetailResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return BaseResponse.ok("User created successfully", userService.createUser(request));
    }

    @PutMapping("/{id}/roles")
    public BaseResponse<UserDetailResponse> assignRoles(@PathVariable Long id, @Valid @RequestBody AssignRolesRequest request) {
        return BaseResponse.ok("Roles assigned successfully", userService.assignRoles(id, request));
    }
}
