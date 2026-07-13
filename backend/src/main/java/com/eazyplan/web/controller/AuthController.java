package com.eazyplan.web.controller;

import com.eazyplan.domain.entities.User;
import com.eazyplan.service.UserService;
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
 * Endpoints de autenticación (registro/login).
 *
 * <p>Paso 1: solo {@code POST /api/auth/register}. El login con JWT llega en el Paso 5.
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
}
