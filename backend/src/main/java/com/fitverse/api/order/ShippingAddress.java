package com.fitverse.api.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Shipping address snapshotted directly onto {@link Order} at checkout time
 * (via {@code @Embedded}), so an order's shipping record stays accurate even
 * if the customer later edits or deletes the saved address it came from.
 * The user's editable address book is the separate
 * {@link com.fitverse.api.address.Address} entity.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddress {

    @Column(name = "shipping_full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "shipping_line1", nullable = false, length = 255)
    private String line1;

    @Column(name = "shipping_city", nullable = false, length = 100)
    private String city;

    @Column(name = "shipping_zip", nullable = false, length = 20)
    private String zip;

    @Column(name = "shipping_country", nullable = false, length = 100)
    private String country;
}
