package com.yourcompany.salesmanagement.module.product.service.impl;

import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import org.springframework.http.HttpStatus;

final class SecurityUtils {
    private SecurityUtils() {}

    static UserPrincipal requirePrincipal() {
        return com.yourcompany.salesmanagement.common.security.SecurityUtils.requirePrincipal();
    }

    static Long requireStoreId(UserPrincipal principal) {
        if (principal.storeId() == null) {
            throw new BusinessException("Store context is missing", HttpStatus.FORBIDDEN);
        }
        return principal.storeId();
    }

    static Long requireBranchId(UserPrincipal principal) {
        if (principal.branchId() == null) {
            throw new BusinessException("Branch context is required for this operation", HttpStatus.BAD_REQUEST);
        }
        return principal.branchId();
    }
}

