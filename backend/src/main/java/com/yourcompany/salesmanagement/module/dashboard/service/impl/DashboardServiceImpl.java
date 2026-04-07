package com.yourcompany.salesmanagement.module.dashboard.service.impl;

import com.yourcompany.salesmanagement.module.dashboard.dto.response.DashboardSummaryResponse;
import com.yourcompany.salesmanagement.module.dashboard.service.DashboardService;
import com.yourcompany.salesmanagement.module.product.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ProductService productService;

    public DashboardServiceImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public DashboardSummaryResponse getSummary() {
        int totalProducts = productService.getAllProducts().size();
        int lowStockProducts = (int) productService.getAllProducts().stream()
                .filter(product -> product.stock() < 50)
                .count();

        return new DashboardSummaryResponse(
                totalProducts,
                lowStockProducts,
                152,
                18500000,
                12
        );
    }
}
