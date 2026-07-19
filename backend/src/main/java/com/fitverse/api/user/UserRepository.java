package com.fitverse.api.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository — Hibernate/Spring generate the implementation
 * at runtime against MySQL. {@code save}, {@code findById}, {@code findAll}
 * and {@code deleteById} are inherited from {@link JpaRepository}; only the
 * two finders below needed to be declared. Was backed by
 * {@code InMemoryUserRepository} in Phase 2.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
