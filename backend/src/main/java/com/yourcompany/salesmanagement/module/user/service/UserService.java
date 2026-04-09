package com.yourcompany.salesmanagement.module.user.service;

import com.yourcompany.salesmanagement.module.user.dto.request.AssignRolesRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.ChangeMyPasswordRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.CreateUserRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.ResetPasswordRequest;
import com.yourcompany.salesmanagement.module.user.dto.request.UpdateUserRequest;
import com.yourcompany.salesmanagement.module.user.dto.response.UserDetailResponse;
import com.yourcompany.salesmanagement.module.user.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getUsers();

    UserDetailResponse getUserById(Long id);

    UserDetailResponse createUser(CreateUserRequest request);

    UserDetailResponse updateUser(Long id, UpdateUserRequest request);

    UserDetailResponse assignRoles(Long userId, AssignRolesRequest request);

    void changeMyPassword(ChangeMyPasswordRequest request);

    void adminResetPassword(Long userId, ResetPasswordRequest request);
}
