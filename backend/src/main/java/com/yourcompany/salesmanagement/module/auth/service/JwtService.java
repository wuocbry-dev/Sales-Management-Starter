package com.yourcompany.salesmanagement.module.auth.service;

import com.yourcompany.salesmanagement.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;
    private final SecretKey key;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId,
                                      String username,
                                      List<String> roleCodes,
                                      Long storeId,
                                      Long branchId) {
        return generateAccessToken(userId, username, roleCodes, null, storeId, branchId);
    }

    public String generateAccessToken(Long userId,
                                      String username,
                                      List<String> roleCodes,
                                      List<String> permissionCodes,
                                      Long storeId,
                                      Long branchId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtProperties.accessTokenTtlSeconds());

        // Keep claims backward compatible: permissionCodes may be absent for older clients/tokens.
        Map<String, Object> claims = permissionCodes == null ? Map.of(
                "userId", userId,
                "username", username,
                "roleCodes", roleCodes,
                "storeId", storeId,
                "branchId", branchId
        ) : Map.of(
                "userId", userId,
                "username", username,
                "roleCodes", roleCodes,
                "permissionCodes", permissionCodes,
                "storeId", storeId,
                "branchId", branchId
        );

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(claims)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

