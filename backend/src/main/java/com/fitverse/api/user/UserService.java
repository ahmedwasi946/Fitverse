package com.fitverse.api.user;

import com.fitverse.api.common.exception.DuplicateResourceException;
import com.fitverse.api.common.exception.ResourceNotFoundException;
import com.fitverse.api.user.dto.UpdateUserRequest;
import com.fitverse.api.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }

    public UserResponse getById(Long id) {
        return toResponse(getEntityById(id));
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse updateProfile(Long id, UpdateUserRequest request) {
        User user = getEntityById(id);
        if (!user.getEmail().equalsIgnoreCase(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with email " + request.email() + " already exists");
        }
        user.setName(request.name());
        user.setEmail(request.email());
        if (request.avatarUrl() != null) {
            user.setAvatarUrl(request.avatarUrl());
        }
        return toResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        getEntityById(id);
        userRepository.deleteById(id);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getRole(), user.getAvatarUrl(), user.getCreatedAt());
    }
}
