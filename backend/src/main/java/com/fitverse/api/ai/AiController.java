package com.fitverse.api.ai;

import com.fitverse.api.ai.dto.*;
import com.fitverse.api.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Placeholder REST surface for every AI feature (AI Avatar, Virtual Try-On,
 * AI Stylist, Smart Recommendations, Outfit Suggestions). Endpoints exist
 * and validate input, but every response comes from {@link AiService}'s
 * stubs — no AI logic is implemented and no AI provider API keys live in
 * this codebase.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/avatar")
    public AvatarResponse createAvatar(@AuthenticationPrincipal UserPrincipal principal,
                                        @Valid @RequestBody AvatarRequest request) {
        return aiService.createAvatar(principal.getId(), request);
    }

    @PostMapping("/try-on")
    public TryOnResponse tryOn(@AuthenticationPrincipal UserPrincipal principal,
                                @Valid @RequestBody TryOnRequest request) {
        return aiService.tryOn(principal.getId(), request);
    }

    @PostMapping("/stylist")
    public AiSuggestionResponse stylist(@AuthenticationPrincipal UserPrincipal principal,
                                         @RequestBody StylistRequest request) {
        return aiService.getStylistRecommendations(principal.getId(), request);
    }

    @GetMapping("/recommendations")
    public AiSuggestionResponse recommendations(@AuthenticationPrincipal UserPrincipal principal) {
        return aiService.getSmartRecommendations(principal.getId());
    }

    @GetMapping("/outfit-suggestions")
    public AiSuggestionResponse outfitSuggestions(@AuthenticationPrincipal UserPrincipal principal,
                                                   @RequestParam Long productId) {
        return aiService.getOutfitSuggestions(principal.getId(), productId);
    }
}
