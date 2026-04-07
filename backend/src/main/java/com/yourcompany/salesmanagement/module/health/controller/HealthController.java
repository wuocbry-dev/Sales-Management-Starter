package com.yourcompany.salesmanagement.module.health.controller;

import com.yourcompany.salesmanagement.common.base.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public BaseResponse<Map<String, Object>> health() {
        return BaseResponse.ok("API is running", Map.of(
                "service", "sales-management-backend",
                "status", "UP",
                "timestamp", OffsetDateTime.now().toString()
        ));
    }
}
