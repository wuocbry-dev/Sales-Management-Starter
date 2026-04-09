package com.yourcompany.salesmanagement.module.dashboard.dto.response;

public record DashboardSummaryResponse(
        double totalSalesToday,
        long totalOrdersToday,
        long totalCustomers,
        long lowStockProducts
) {}
