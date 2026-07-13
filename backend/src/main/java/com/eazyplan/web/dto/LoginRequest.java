package com.eazyplan.web.dto;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo de {@code POST /api/auth/login}. */
public record LoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {}
