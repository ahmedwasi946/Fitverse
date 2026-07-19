package com.fitverse.api.cart;

import com.fitverse.api.cart.dto.AddCartItemRequest;
import com.fitverse.api.cart.dto.CartResponse;
import com.fitverse.api.cart.dto.UpdateCartItemRequest;
import com.fitverse.api.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal UserPrincipal principal) {
        return cartService.getCart(principal.getId());
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponse addItem(@AuthenticationPrincipal UserPrincipal principal,
                                 @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(principal.getId(), request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(@AuthenticationPrincipal UserPrincipal principal,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(principal.getId(), itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long itemId) {
        return cartService.removeItem(principal.getId(), itemId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.getId());
    }
}
