package com.CashFlow.Dashboard.Services;

import com.CashFlow.Dashboard.Entities.User;
import com.CashFlow.Dashboard.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔹 Register a new user
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Default role → USER (if none set)
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    // 🔹 Find user by username (for login)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 🔹 Find user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 🔹 Find user by ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // 🔹 Check if given raw password matches stored hash
    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    // 🔹 Just for testing / admin
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
