package com.fitverse.api.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository — was backed by {@code InMemoryCategoryRepository}
 * in Phase 2; Hibernate/Spring generate the implementation now.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
