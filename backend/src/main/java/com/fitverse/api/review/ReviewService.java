package com.fitverse.api.review;

import com.fitverse.api.common.exception.ResourceNotFoundException;
import com.fitverse.api.product.Product;
import com.fitverse.api.product.ProductService;
import com.fitverse.api.review.dto.ReviewRequest;
import com.fitverse.api.review.dto.ReviewResponse;
import com.fitverse.api.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<ReviewResponse> getForProduct(Long productId) {
        productService.getEntityById(productId); // 404s if the product doesn't exist
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream().map(this::toResponse).toList();
    }

    public ReviewResponse addReview(User author, Long productId, ReviewRequest request) {
        Product product = productService.getEntityById(productId);
        Review review = Review.builder()
                .product(product)
                .user(author)
                .userName(author.getName())
                .rating(request.rating())
                .comment(request.comment())
                .createdAt(Instant.now())
                .build();
        return toResponse(reviewRepository.save(review));
    }

    public void deleteReview(Long reviewId, Long requesterId, boolean requesterIsAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> ResourceNotFoundException.of("Review", reviewId));
        if (!requesterIsAdmin && !review.getUser().getId().equals(requesterId)) {
            throw new AccessDeniedException("You can only delete your own reviews");
        }
        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(r.getId(), r.getProduct().getId(), r.getUserName(), r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
