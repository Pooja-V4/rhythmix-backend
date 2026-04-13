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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.musicapp.musicapp.dto.GoogleAuthRequest;
import org.springframework.beans.factory.annotation.Value;
import java.util.Collections;

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

    @Value("${google.client.id}")
    private String googleClientId;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleAuthRequest request) {
        try {
            // Verify the Google token is real and valid
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());

            if (idToken == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid Google token"));
            }

            // Extract user info from Google token
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String googleId = payload.getSubject();

            // Find existing user or create new one
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                // New Google user — create account automatically
                User newUser = User.builder()
                        .name(name != null ? name : email.split("@")[0])
                        .email(email)
                        // Random password — Google users don't need it
                        .password(passwordEncoder.encode(googleId + "_google_oauth"))
                        .build();
                return userRepository.save(newUser);
            });

            // Generate your JWT token
            String token = jwtUtil.generateToken(user.getEmail(), user.getId());

            return ResponseEntity.ok(new LoginResponse(
                    token,
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            ));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Google authentication failed"));
        }
    }

}