package com.eazyplan.service;

import com.eazyplan.domain.entities.User;
import com.eazyplan.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Lógica de negocio de usuarios.
 *
 * <p>Migra el {@code UserService} POJO del legado a un bean gestionado por Spring
 * (inyección por constructor + {@code @Transactional}). Las contraseñas ahora se
 * hashean con BCrypt.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String name, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        User user = new User(username, name, email, passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }
}
