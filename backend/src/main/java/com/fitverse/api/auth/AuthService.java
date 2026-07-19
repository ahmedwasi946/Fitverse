package com.fitverse.api.auth;

import com.fitverse.api.auth.dto.AuthResponse;
import com.fitverse.api.auth.dto.LoginRequest;
import com.fitverse.api.auth.dto.RegisterRequest;
import com.fitverse.api.common.exception.DuplicateResourceException;
import com.fitverse.api.security.JwtService;
import com.fitverse.api.security.UserPrincipal;
import com.fitverse.api.user.Role;
import com.fitverse.api.user.User;
import com.fitverse.api.user.UserRepository;
import com.fitverse.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("An account with email " + request.email() + " already exists");
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .createdAt(Instant.now())
                .build();
        user = userRepository.save(user);

        String token = jwtService.generateToken(new UserPrincipal(user));
        return AuthResponse.of(token, userService.toResponse(user));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Authenticated user vanished unexpectedly"));

        String token = jwtService.generateToken(new UserPrincipal(user));
        return AuthResponse.of(token, userService.toResponse(user));
    }
}
