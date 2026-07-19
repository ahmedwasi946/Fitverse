package com.fitverse.api.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository — was backed by {@code InMemoryProductRepository}
 * in Phase 2. {@code findByCategoryId} resolves against the nested
 * {@code category.id} property even though there's no literal
 * {@code categoryId} field on {@link Product} — Spring Data's query
 * derivation walks the {@code category} relationship automatically.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
}
