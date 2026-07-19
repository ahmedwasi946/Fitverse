package com.fitverse.api.address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository — Hibernate/Spring generate the implementation
 * at runtime. No manual implementation class exists for this repository.
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
}
