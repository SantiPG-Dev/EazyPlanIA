package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.User;

/**
 * Respuesta pública de un usuario. Nunca expone la contraseña.
 */
public record UserResponse(Long id, String username, String name, String email, String role) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getRole().name());
    }
}
