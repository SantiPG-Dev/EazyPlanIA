package com.eazyplan.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de integración del endpoint de registro (entregable del Paso 1).
 *
 * <p>Verifica: 201 en registro válido, 409 en duplicado y 400 en datos inválidos,
 * todo contra H2 en memoria.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    private static final String REGISTER_BODY_TEMPLATE =
            "{\"username\":\"%s\",\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}";

    @Test
    void register_returns201_whenValid() throws Exception {
        String body = REGISTER_BODY_TEMPLATE.formatted("santi", "Santiago", "santi@test.com", "secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("santi"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void register_returns409_whenUsernameTaken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY_TEMPLATE.formatted("dup", "Dup", "dup1@test.com", "secret123")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_BODY_TEMPLATE.formatted("dup", "Dup2", "dup2@test.com", "secret123")))
                .andExpect(status().isConflict());
    }

    @Test
    void register_returns400_whenInvalid() throws Exception {
        String body = REGISTER_BODY_TEMPLATE.formatted("ab", "", "notanemail", "123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ── Login (Paso 5) ────────────────────────────────────────────────────

    @Test
    void login_returns200_withJwt_whenValidCredentials() throws Exception {
        // Registrar primero
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(REGISTER_BODY_TEMPLATE.formatted("loginuser", "Login", "login@test.com", "secret123")))
                .andExpect(status().isCreated());

        // Login
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"loginuser\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.username").value("loginuser"))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    void login_returns401_whenWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(REGISTER_BODY_TEMPLATE.formatted("badpw", "Bad", "badpw@test.com", "secret123")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"badpw\",\"password\":\"WRONG\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_returns401_whenUnknownUser() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"ghost\",\"password\":\"secret123\"}"))
                .andExpect(status().isUnauthorized());
    }
}
