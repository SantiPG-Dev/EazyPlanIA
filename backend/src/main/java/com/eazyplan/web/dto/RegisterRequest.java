package com.eazyplan.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de la petición {@code POST /api/auth/register}.
 */
public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 30) String username,
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password
) {}
