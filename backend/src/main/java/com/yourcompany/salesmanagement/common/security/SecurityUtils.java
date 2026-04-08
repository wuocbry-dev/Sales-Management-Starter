package com.yourcompany.salesmanagement.common.security;

import com.yourcompany.salesmanagement.exception.BusinessException;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static UserPrincipal requirePrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BusinessException("Unauthenticated", HttpStatus.UNAUTHORIZED);
        }
        return principal;
    }

    public static Long requireStoreId() {
        Long storeId = requirePrincipal().storeId();
        if (storeId == null) {
            throw new BusinessException("Store context is missing", HttpStatus.FORBIDDEN);
        }
        return storeId;
    }
}

