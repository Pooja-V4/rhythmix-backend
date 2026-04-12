package com.musicapp.musicapp.service;

import com.musicapp.musicapp.dto.*;
import com.musicapp.musicapp.entity.User;
import com.musicapp.musicapp.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final FavoriteRepository favoriteRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Removed SongRepository — not needed for profile stats
    public UserService(UserRepository userRepository,
                       PlaylistRepository playlistRepository,
                       FavoriteRepository favoriteRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.favoriteRepository = favoriteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    // ✅ Get profile with stats
    public ProfileResponse getProfile(Long userId) {
        User user = getUserById(userId);

        int totalPlaylists = playlistRepository.findByUserId(userId).size();
        int totalFavorites = favoriteRepository.findByUserId(userId).size();

        // Count total songs across all user's playlists
        int totalSongs = playlistRepository.findByUserId(userId)
                .stream()
                .mapToInt(p -> p.getSongs() != null ? p.getSongs().size() : 0)
                .sum();

        return new ProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                totalPlaylists,
                totalFavorites,
                totalSongs
        );
    }

    // ✅ Update name
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getUserById(userId);
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        return userRepository.save(user);
    }

    // ✅ Change password
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        if (request.getNewPassword().length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ✅ Delete account
    public void deleteAccount(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }
}