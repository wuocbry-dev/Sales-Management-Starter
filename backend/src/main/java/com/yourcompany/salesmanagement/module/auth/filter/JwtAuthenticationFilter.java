package com.yourcompany.salesmanagement.module.auth.filter;

import com.yourcompany.salesmanagement.module.auth.service.JwtService;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
import com.yourcompany.salesmanagement.module.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.parseClaims(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.get("username", String.class);
            Long storeId = claims.get("storeId", Long.class);
            Long branchId = claims.get("branchId", Long.class);

            @SuppressWarnings("unchecked")
            List<String> roleCodes = claims.get("roleCodes", List.class);
            if (roleCodes == null) roleCodes = Collections.emptyList();

            // Permission codes are optional claim for backward compatibility.
            @SuppressWarnings("unchecked")
            List<String> permissionCodes = claims.get("permissionCodes", List.class);
            if (permissionCodes == null) {
                permissionCodes = loadPermissionCodesFromDb(userId);
            }

            Set<SimpleGrantedAuthority> authorities = buildAuthorities(roleCodes, permissionCodes);

            var principal = new UserPrincipal(userId, username, storeId, branchId, roleCodes, permissionCodes, authorities);
            var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ignored) {
            // Invalid token: let the chain continue; Security will reject if endpoint requires auth
        }

        filterChain.doFilter(request, response);
    }

    private Set<SimpleGrantedAuthority> buildAuthorities(List<String> roleCodes, List<String> permissionCodes) {
        var list = new ArrayList<SimpleGrantedAuthority>();
        if (roleCodes != null) {
            for (String code : roleCodes) {
                if (code == null) continue;
                String t = code.trim();
                if (t.isEmpty()) continue;
                list.add(new SimpleGrantedAuthority("ROLE_" + t));
            }
        }
        if (permissionCodes != null) {
            for (String code : permissionCodes) {
                if (code == null) continue;
                String t = code.trim();
                if (t.isEmpty()) continue;
                list.add(new SimpleGrantedAuthority(t));
            }
        }
        return list.stream().collect(Collectors.toSet());
    }

    private List<String> loadPermissionCodesFromDb(Long userId) {
        if (userId == null) return Collections.emptyList();
        return userRepository.findByIdWithRolesAndPermissions(userId)
                .map(u -> u.getRoles().stream()
                        .flatMap(r -> r.getPermissions().stream())
                        .map(p -> p.getCode())
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .distinct()
                        .toList())
                .orElse(Collections.emptyList());
    }
}

