package com.fitverse.api.wishlist;

import com.fitverse.api.product.Product;
import com.fitverse.api.product.ProductService;
import com.fitverse.api.user.User;
import com.fitverse.api.user.UserService;
import com.fitverse.api.wishlist.dto.WishlistItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductService productService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId).stream()
                .map(item -> toResponse(item.getProduct()))
                .toList();
    }

    public List<WishlistItemResponse> addItem(Long userId, Long productId) {
        Product product = productService.getEntityById(productId); // 404s if the product doesn't exist
        if (wishlistRepository.findByUserIdAndProductId(userId, productId).isEmpty()) {
            User user = userService.getEntityById(userId);
            WishlistItem item = WishlistItem.builder()
                    .user(user)
                    .product(product)
                    .addedAt(Instant.now())
                    .build();
            wishlistRepository.save(item);
        }
        return getWishlist(userId);
    }

    public List<WishlistItemResponse> removeItem(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        return getWishlist(userId);
    }

    private WishlistItemResponse toResponse(Product p) {
        return new WishlistItemResponse(p.getId(), p.getName(), p.getBrand(), p.getPrimaryImageUrl(), p.getPrice(), p.getSalePrice());
    }
}
