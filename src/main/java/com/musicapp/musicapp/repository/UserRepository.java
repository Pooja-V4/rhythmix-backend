package com.musicapp.musicapp.repository;

import com.musicapp.musicapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository gives you save(), findAll(), findById(), delete() for FREE
    // You just add custom queries here if needed

    // Check if email already exists (for registration)
    boolean existsByEmail(String email);

    // Find user by email (for login later)
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetPasswordToken(String token);
}