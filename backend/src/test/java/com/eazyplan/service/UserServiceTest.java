package com.eazyplan.service;

import com.eazyplan.domain.entities.User;
import com.eazyplan.repository.UserRepository;
import com.eazyplan.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de {@link UserService}: registro (conflictos BCrypt) y login (JWT).
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtService jwtService;
    @InjectMocks UserService userService;

    @Test
    void register_encodesPasswordAndSaves() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("rawpass")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.register("newuser", "New", "new@test.com", "rawpass");

        assertEquals("hashed", result.getPassword());
        verify(userRepository).save(any());
    }

    @Test
    void register_throws409_whenUsernameExists() {
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        var ex = assertThrows(ResponseStatusException.class,
                () -> userService.register("taken", "T", "t@test.com", "pw"));
        assertEquals(409, ex.getStatusCode().value());
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_generatesToken_whenCredentialsMatch() {
        User user = new User("valid", "Valid", "v@test.com", "hashed");
        user.setId(7L);
        when(userRepository.findByUsername("valid")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawpass", "hashed")).thenReturn(true);
        when(jwtService.generateToken("valid", 7L)).thenReturn("jwt-token");

        UserService.LoginResult result = userService.login("valid", "rawpass");

        assertEquals("jwt-token", result.token());
        assertEquals(7L, result.user().getId());
    }

    @Test
    void login_throws401_whenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        var ex = assertThrows(ResponseStatusException.class,
                () -> userService.login("ghost", "pw"));
        assertEquals(401, ex.getStatusCode().value());
    }

    @Test
    void login_throws401_whenPasswordMismatch() {
        User user = new User("valid", "V", "v@test.com", "hashed");
        when(userRepository.findByUsername("valid")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        var ex = assertThrows(ResponseStatusException.class,
                () -> userService.login("valid", "wrong"));
        assertEquals(401, ex.getStatusCode().value());
        verify(jwtService, never()).generateToken(any(), any());
    }
}
