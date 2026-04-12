package com.musicapp.musicapp.controller;

import com.musicapp.musicapp.dto.*;
import com.musicapp.musicapp.entity.User;
import com.musicapp.musicapp.repository.UserRepository;
import com.musicapp.musicapp.security.JwtUtil;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            // Return consistent JSON error — not plain string
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Email already registered"));
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved.getEmail(), saved.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new LoginResponse(
                        token,
                        saved.getId(),
                        saved.getName(),
                        saved.getEmail()
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow();

            String token = jwtUtil.generateToken(user.getEmail(), user.getId());

            return ResponseEntity.ok(new LoginResponse(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            ));

        } catch (AuthenticationException e) {
            // Return consistent JSON error
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }
    }
}