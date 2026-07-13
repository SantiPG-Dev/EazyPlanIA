package com.eazyplan.web.controller;

import com.eazyplan.domain.entities.User;
import com.eazyplan.service.UserService;
import com.eazyplan.web.dto.AuthResponse;
import com.eazyplan.web.dto.LoginRequest;
import com.eazyplan.web.dto.RegisterRequest;
import com.eazyplan.web.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Endpoints de autenticación: registro y login.
 *
 * <pre>
 * POST /api/auth/register → 201 UserResponse
 * POST /api/auth/login    → 200 AuthResponse (JWT)
 * </pre>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(
                request.username(), request.name(), request.email(), request.password());
        return ResponseEntity
                .created(URI.create("/api/users/" + user.getId()))
                .body(UserResponse.from(user));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        UserService.LoginResult result = userService.login(request.username(), request.password());
        return AuthResponse.of(result.token(), UserResponse.from(result.user()));
    }
}
