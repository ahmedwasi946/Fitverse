package com.fitverse.api.wishlist;

import com.fitverse.api.security.UserPrincipal;
import com.fitverse.api.wishlist.dto.WishlistItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public List<WishlistItemResponse> getWishlist(@AuthenticationPrincipal UserPrincipal principal) {
        return wishlistService.getWishlist(principal.getId());
    }

    @PostMapping("/{productId}")
    public List<WishlistItemResponse> addItem(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long productId) {
        return wishlistService.addItem(principal.getId(), productId);
    }

    @DeleteMapping("/{productId}")
    public List<WishlistItemResponse> removeItem(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long productId) {
        return wishlistService.removeItem(principal.getId(), productId);
    }
}
