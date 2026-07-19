package com.fitverse.api.cart;

import com.fitverse.api.cart.dto.AddCartItemRequest;
import com.fitverse.api.cart.dto.CartItemResponse;
import com.fitverse.api.cart.dto.CartResponse;
import com.fitverse.api.cart.dto.UpdateCartItemRequest;
import com.fitverse.api.common.exception.BadRequestException;
import com.fitverse.api.common.exception.ResourceNotFoundException;
import com.fitverse.api.product.Product;
import com.fitverse.api.product.ProductService;
import com.fitverse.api.user.User;
import com.fitverse.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private static final BigDecimal FREE_SHIPPING_THRESHOLD = new BigDecimal("150.00");
    private static final BigDecimal FLAT_SHIPPING_FEE = new BigDecimal("12.00");

    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        return buildResponse(items);
    }

    public CartResponse addItem(Long userId, AddCartItemRequest request) {
        Product product = productService.getEntityById(request.productId());
        if (!product.getSizes().contains(request.size())) {
            throw new BadRequestException("Size " + request.size() + " is not available for this product");
        }

        CartItem existing = cartItemRepository
                .findByUserIdAndProductIdAndSize(userId, request.productId(), request.size())
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.quantity());
            cartItemRepository.save(existing);
        } else {
            User user = userService.getEntityById(userId);
            CartItem item = CartItem.builder()
                    .user(user)
                    .product(product)
                    .size(request.size())
                    .quantity(request.quantity())
                    .addedAt(Instant.now())
                    .build();
            cartItemRepository.save(item);
        }
        return getCart(userId);
    }

    public CartResponse updateItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        CartItem item = getOwnedItem(userId, itemId);
        item.setQuantity(request.quantity());
        cartItemRepository.save(item);
        return getCart(userId);
    }

    public CartResponse removeItem(Long userId, Long itemId) {
        CartItem item = getOwnedItem(userId, itemId);
        cartItemRepository.deleteById(item.getId());
        return getCart(userId);
    }

    public void clearCart(Long userId) {
        cartItemRepository.deleteAllByUserId(userId);
    }

    private CartItem getOwnedItem(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> ResourceNotFoundException.of("Cart item", itemId));
        if (!item.getUser().getId().equals(userId)) {
            throw ResourceNotFoundException.of("Cart item", itemId);
        }
        return item;
    }

    private CartResponse buildResponse(List<CartItem> items) {
        List<CartItemResponse> lines = items.stream().map(this::toItemResponse).toList();
        BigDecimal subtotal = lines.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shipping = subtotal.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : (subtotal.compareTo(FREE_SHIPPING_THRESHOLD) >= 0 ? BigDecimal.ZERO : FLAT_SHIPPING_FEE);
        BigDecimal total = subtotal.add(shipping);
        return new CartResponse(lines, subtotal, shipping, total);
    }

    private CartItemResponse toItemResponse(CartItem item) {
        Product product = item.getProduct();
        BigDecimal unitPrice = product.getSalePrice() != null ? product.getSalePrice() : product.getPrice();
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(item.getId(), product.getId(), product.getName(), product.getBrand(),
                product.getPrimaryImageUrl(), item.getSize(), item.getQuantity(), unitPrice, lineTotal);
    }
}
