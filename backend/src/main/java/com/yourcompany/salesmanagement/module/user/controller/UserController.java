package com.yourcompany.salesmanagement.module.user.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import com.yourcompany.salesmanagement.module.user.dto.response.UserResponse;
import com.yourcompany.salesmanagement.module.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
