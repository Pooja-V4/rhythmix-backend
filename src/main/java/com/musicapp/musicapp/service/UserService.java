package com.musicapp.musicapp.service;

import com.musicapp.musicapp.entity.User;
import com.musicapp.musicapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service  // Marks this as a Spring-managed service bean
public class UserService {

    // Constructor injection (better than @Autowired on field)
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Register a new user
    public User registerUser(User user) {
        // Check if email is already taken
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        // NOTE: password stored as plain text for now, we hash it in Phase 5
        return userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get one user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}