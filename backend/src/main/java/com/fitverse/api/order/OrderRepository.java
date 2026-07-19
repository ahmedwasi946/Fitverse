package com.fitverse.api.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository — was backed by {@code InMemoryOrderRepository}
 * in Phase 2. {@code findAll(Sort)} (used for the admin "all orders" view)
 * is inherited from {@link JpaRepository} directly, no declaration needed.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
