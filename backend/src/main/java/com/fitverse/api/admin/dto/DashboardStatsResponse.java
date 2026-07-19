package com.fitverse.api.admin.dto;

import java.math.BigDecimal;

public record DashboardStatsResponse(
        long totalUsers,
        long totalProducts,
        long totalCategories,
        long totalOrders,
        long pendingOrders,
        BigDecimal totalRevenue
) {
}
