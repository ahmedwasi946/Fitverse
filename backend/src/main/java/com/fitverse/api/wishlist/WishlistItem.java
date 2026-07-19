package com.fitverse.api.wishlist;

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
 * JPA entity backed by the {@code wishlist_items} MySQL table (Phase 3) —
 * {@code userId}/{@code productId} became real {@code @ManyToOne} foreign keys.
 */
@Entity
@Table(name = "wishlist_items",
        indexes = @Index(name = "idx_wishlist_items_user_id", columnList = "user_id"),
        uniqueConstraints = @UniqueConstraint(name = "uq_wishlist_user_product", columnNames = {"user_id", "product_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Instant addedAt;
}
