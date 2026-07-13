package com.eazyplan.web.dto;

/** Respuesta de {@code POST /api/auth/login}: token JWT + datos del usuario. */
public record AuthResponse(String token, String tokenType, UserResponse user) {

    public static AuthResponse of(String token, UserResponse user) {
        return new AuthResponse(token, "Bearer", user);
    }
}
