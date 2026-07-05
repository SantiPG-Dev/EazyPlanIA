package com.eazyplan.domain.services;

import com.eazyplan.domain.entities.User;
import com.eazyplan.domain.repositories.UserRepository;
import com.eazyplan.domain.repositories.UserRepositoryImpl;

public class UserService {
    private final UserRepository userRepository = UserRepositoryImpl.get();
    
    public User register(String username, String name, String email, String password) throws Exception {
        if (userRepository.existsByUsername(username)) throw new IllegalArgumentException("Username already exists");
        if (userRepository.existsByEmail(email)) throw new IllegalArgumentException("Email already registered");

        User user = new User(username, name, email, password);
        userRepository.save(user);
        return userRepository.findById(user.getId());
    }

    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return true;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id);
    }

    public void update(User user, String name, String email) {
        if (name != null) user.setName(name);
        if (email != null) user.setEmail(email);
        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
