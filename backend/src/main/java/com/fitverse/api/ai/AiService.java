package com.fitverse.api.ai;

import com.fitverse.api.ai.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Every method here is an intentional placeholder. None of them call an
 * external AI provider and no API keys are referenced anywhere in this
 * project — that integration is left for you to add later, per the brief.
 * Each TODO marks exactly where real model-calling logic should go.
 */
@Service
public class AiService {

    public AvatarResponse createAvatar(Long userId, AvatarRequest request) {
        // TODO: send request.photoBase64() to an AI avatar-generation provider,
        //       store the resulting image (e.g. in blob storage) and persist
        //       its URL on the user's profile.
        return new AvatarResponse("AI Avatar generation is not implemented yet.", null);
    }

    public TryOnResponse tryOn(Long userId, TryOnRequest request) {
        // TODO: send request.photoBase64() + the product's image to a virtual
        //       try-on model and return the composited preview image URL.
        return new TryOnResponse("Virtual Try-On is not implemented yet.", null);
    }

    public AiSuggestionResponse getStylistRecommendations(Long userId, StylistRequest request) {
        // TODO: send the user's stated preferences (occasion/styles/budget)
        //       plus their purchase/browsing history to a recommendation
        //       model and return the product ids it suggests.
        return new AiSuggestionResponse("AI Stylist is not implemented yet.", List.of());
    }

    public AiSuggestionResponse getSmartRecommendations(Long userId) {
        // TODO: generate personalised product recommendations for this user
        //       (e.g. collaborative filtering, embedding similarity, etc).
        return new AiSuggestionResponse("Smart Recommendations are not implemented yet.", List.of());
    }

    public AiSuggestionResponse getOutfitSuggestions(Long userId, Long productId) {
        // TODO: given an anchor product, suggest complementary products that
        //       form a complete outfit.
        return new AiSuggestionResponse("Outfit Suggestions are not implemented yet.", List.of());
    }
}
