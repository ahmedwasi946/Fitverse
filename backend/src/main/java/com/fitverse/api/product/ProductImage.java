package com.fitverse.api.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single image belonging to a {@link Product}. JPA entity backed by the
 * {@code product_images} MySQL table — the "Images" table from the Phase 3
 * brief. Phase 2 only had a single {@code Product.imageUrl} string; this
 * normalises that into a proper one-to-many relationship so a product can
 * carry a full gallery.
 */
@Entity
@Table(name = "product_images", indexes = {
        @Index(name = "idx_product_images_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}
