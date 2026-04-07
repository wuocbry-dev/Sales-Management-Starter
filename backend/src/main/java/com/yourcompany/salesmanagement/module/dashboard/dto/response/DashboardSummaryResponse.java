package com.yourcompany.salesmanagement.module.dashboard.dto.response;

public record DashboardSummaryResponse(
        int totalProducts,
        int lowStockProducts,
        int totalCustomers,
        double todayRevenue,
        int pendingOrders
) {}
