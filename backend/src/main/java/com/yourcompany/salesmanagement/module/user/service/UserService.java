package com.yourcompany.salesmanagement.module.user.service;

import com.yourcompany.salesmanagement.module.user.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getUsers();
}
