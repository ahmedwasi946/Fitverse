package com.fitverse.api.order;

import com.fitverse.api.cart.CartService;
import com.fitverse.api.cart.dto.CartItemResponse;
import com.fitverse.api.cart.dto.CartResponse;
import com.fitverse.api.common.exception.BadRequestException;
import com.fitverse.api.common.exception.ResourceNotFoundException;
import com.fitverse.api.order.dto.*;
import com.fitverse.api.product.Product;
import com.fitverse.api.product.ProductService;
import com.fitverse.api.user.User;
import com.fitverse.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;

    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        CartResponse cart = cartService.getCart(userId);
        if (cart.items().isEmpty()) {
            throw new BadRequestException("Your bag is empty — add something before checking out");
        }

        User user = userService.getEntityById(userId);

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .shippingAddress(toShippingAddress(request.shippingAddress()))
                .subtotal(cart.subtotal())
                .shipping(cart.shipping())
                .total(cart.total())
                .status(OrderStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        cart.items().stream().map(this::toOrderItem).forEach(order::addItem);

        order = orderRepository.save(order);
        cartService.clearCart(userId);
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderForUser(Long userId, Long orderId) {
        Order order = getEntityById(orderId);
        if (!order.getUser().getId().equals(userId)) {
            throw ResourceNotFoundException.of("Order", orderId);
        }
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream().map(this::toResponse).toList();
    }

    public OrderResponse updateStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = getEntityById(orderId);
        order.setStatus(request.status());
        return toResponse(orderRepository.save(order));
    }

    private Order getEntityById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Order", id));
    }

    private String generateOrderNumber() {
        return "FV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private OrderItem toOrderItem(CartItemResponse item) {
        Product product = productService.getEntityById(item.productId());
        return OrderItem.builder()
                .product(product)
                .productName(item.name())
                .imageUrl(item.imageUrl())
                .size(item.size())
                .quantity(item.quantity())
                .unitPrice(item.unitPrice())
                .build();
    }

    private ShippingAddress toShippingAddress(AddressDto dto) {
        return ShippingAddress.builder()
                .fullName(dto.fullName())
                .line1(dto.line1())
                .city(dto.city())
                .zip(dto.zip())
                .country(dto.country())
                .build();
    }

    private AddressDto toAddressDto(ShippingAddress address) {
        return new AddressDto(address.getFullName(), address.getLine1(), address.getCity(), address.getZip(), address.getCountry());
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(i -> new OrderItemResponse(i.getProduct().getId(), i.getProductName(), i.getImageUrl(), i.getSize(), i.getQuantity(), i.getUnitPrice()))
                .toList();
        return new OrderResponse(order.getId(), order.getOrderNumber(), items, toAddressDto(order.getShippingAddress()),
                order.getSubtotal(), order.getShipping(), order.getTotal(), order.getStatus(), order.getCreatedAt());
    }
}
