package com.fitverse.api.cart;

import com.fitverse.api.product.Product;
import com.fitverse.api.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * A single line in a user's shopping bag. JPA entity backed by the
 * {@code cart_items} MySQL table (Phase 3) — {@code userId}/{@code productId}
 * became real {@code @ManyToOne} foreign keys.
 */
@Entity
@Table(name = "cart_items", indexes = {
        @Index(name = "idx_cart_items_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 10)
    private String size;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Instant addedAt;
}
