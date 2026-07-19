package com.fitverse.api.product;

import com.fitverse.api.category.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity backed by the {@code products} MySQL table (Phase 3) — was a
 * plain in-memory model in Phase 2. {@code categoryId} became a real
 * {@code @ManyToOne} relationship, and the single {@code imageUrl} string
 * became a proper {@code @OneToMany} gallery via {@link ProductImage}.
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_category_id", columnList = "category_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal salePrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "size", length = 10)
    @Builder.Default
    private List<String> sizes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    private Integer fitConfidence;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(length = 300)
    private String material;

    @Column(length = 300)
    private String shippingInfo;

    @Column(length = 300)
    private String returnsInfo;

    @Column(length = 300)
    private String careInfo;

    @Column(nullable = false)
    private Instant createdAt;

    /** Keeps both sides of the relationship in sync (required for cascade/orphanRemoval to behave). */
    public void addImage(String url, int sortOrder) {
        ProductImage image = ProductImage.builder().product(this).url(url).sortOrder(sortOrder).build();
        this.images.add(image);
    }

    /** Convenience accessor for callers (cart/wishlist/order line items) that just need one representative thumbnail. */
    public String getPrimaryImageUrl() {
        return images.isEmpty() ? null : images.get(0).getUrl();
    }
}
