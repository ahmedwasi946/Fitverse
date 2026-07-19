package com.fitverse.api.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository — was backed by {@code InMemoryCartItemRepository}
 * in Phase 2. Every finder here resolves against nested {@code user.id} /
 * {@code product.id} properties via Spring Data's query derivation.
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndProductIdAndSize(Long userId, Long productId, String size);
    void deleteAllByUserId(Long userId);
}
