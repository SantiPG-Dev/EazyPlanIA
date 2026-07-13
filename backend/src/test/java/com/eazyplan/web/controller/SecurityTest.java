package com.eazyplan.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de la cadena de filtros de Spring Security (Paso 5).
 *
 * <p>Verifica que los endpoints protegidos devuelven 401 sin token,
 * 200 con token válido, y que /api/auth/** es público.
 */
@SpringBootTest
@AutoConfigureMockMvc  // Filtros ACTIVADOS: prueba el comportamiento real de seguridad
class SecurityTest {

    @Autowired
    MockMvc mockMvc;

    /** Extrae el token JWT de la respuesta de login. */
    private String loginAndGetToken(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"%s\",\"password\":\"%s\"}".formatted(username, password)))
                .andReturn();
        return result.getResponse().getContentAsString()
                .replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    private void register(String username, String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"%s\",\"name\":\"Sec\",\"email\":\"%s\",\"password\":\"secret123\"}"
                        .formatted(username, email)))
                .andExpect(status().isCreated());
    }

    @Test
    void protectedEndpoint_returns401_withoutToken() throws Exception {
        mockMvc.perform(get("/api/diets/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_returns200_withValidToken() throws Exception {
        register("sectoken_%s".formatted(System.nanoTime()), "sec_%s@test.com".formatted(System.nanoTime()));

        // Necesitamos el userId: registramos y hacemos login
        String uname = "authtest_%s".formatted(System.nanoTime());
        register(uname, "auth_%s@test.com".formatted(System.nanoTime()));
        String token = loginAndGetToken(uname, "secret123");

        mockMvc.perform(get("/api/users/1/diets")  // cualquier endpoint /api/** funciona con token
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoint_returns401_withGarbageToken() throws Exception {
        mockMvc.perform(get("/api/users/1/diets")
                .header("Authorization", "Bearer not.a.real.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authEndpoints_arePublic() throws Exception {
        // /api/auth/register y /api/auth/login NO requieren token
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"x\",\"password\":\"x\"}"))
                .andExpect(status().isUnauthorized()); // 401 por credenciales, no por falta de token
    }
}
