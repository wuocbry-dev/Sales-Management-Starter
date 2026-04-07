package com.yourcompany.salesmanagement.module.auth.filter;

import com.yourcompany.salesmanagement.module.auth.service.JwtService;
import com.yourcompany.salesmanagement.module.auth.service.dto.UserPrincipal;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
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

            var authorities = roleCodes.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
                    .collect(Collectors.toSet());

            var principal = new UserPrincipal(userId, username, storeId, branchId, roleCodes, authorities);
            var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ignored) {
            // Invalid token: let the chain continue; Security will reject if endpoint requires auth
        }

        filterChain.doFilter(request, response);
    }
}

