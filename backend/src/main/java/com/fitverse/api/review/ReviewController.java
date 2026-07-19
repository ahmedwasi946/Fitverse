package com.fitverse.api.review;

import com.fitverse.api.review.dto.ReviewRequest;
import com.fitverse.api.review.dto.ReviewResponse;
import com.fitverse.api.security.UserPrincipal;
import com.fitverse.api.user.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/api/products/{productId}/reviews")
    public List<ReviewResponse> getForProduct(@PathVariable Long productId) {
        return reviewService.getForProduct(productId);
    }

    @PostMapping("/api/products/{productId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse addReview(@AuthenticationPrincipal UserPrincipal principal,
                                     @PathVariable Long productId,
                                     @Valid @RequestBody ReviewRequest request) {
        return reviewService.addReview(principal.getUser(), productId, request);
    }

    @DeleteMapping("/api/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@AuthenticationPrincipal UserPrincipal principal, @PathVariable Long id) {
        boolean isAdmin = principal.getUser().getRole() == Role.ROLE_ADMIN;
        reviewService.deleteReview(id, principal.getId(), isAdmin);
    }
}
