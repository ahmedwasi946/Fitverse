package com.fitverse.api.order;

import com.fitverse.api.order.dto.OrderResponse;
import com.fitverse.api.order.dto.OrderStatusUpdateRequest;
import com.fitverse.api.order.dto.PlaceOrderRequest;
import com.fitverse.api.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@AuthenticationPrincipal UserPrincipal principal,
                                     @Valid @RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(principal.getId(), request);
    }

    @GetMapping("/api/orders")
    public List<OrderResponse> getMyOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return orderService.getOrdersForUser(principal.getId());
    }

    @GetMapping("/api/orders/{id}")
    public OrderResponse getMyOrder(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        return orderService.getOrderForUser(principal.getId(), id);
    }

    @GetMapping("/api/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/api/admin/orders/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateStatus(id, request);
    }
}
