package com.fitverse.api.admin;

import com.fitverse.api.admin.dto.DashboardStatsResponse;
import com.fitverse.api.category.CategoryRepository;
import com.fitverse.api.order.Order;
import com.fitverse.api.order.OrderRepository;
import com.fitverse.api.order.OrderStatus;
import com.fitverse.api.product.ProductRepository;
import com.fitverse.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;

    public DashboardStatsResponse getDashboardStats() {
        List<Order> orders = orderRepository.findAll();

        long pending = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.PROCESSING)
                .count();

        BigDecimal revenue = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.CANCELLED)
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardStatsResponse(
                userRepository.count(),
                productRepository.count(),
                categoryRepository.count(),
                orders.size(),
                pending,
                revenue
        );
    }
}
