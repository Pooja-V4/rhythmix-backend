package com.musicapp.musicapp.controller;

import com.musicapp.musicapp.dto.*;
import com.musicapp.musicapp.entity.User;
import com.musicapp.musicapp.repository.UserRepository;
import com.musicapp.musicapp.security.JwtUtil;
import com.musicapp.musicapp.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

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
    private final EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    // Register — send verification email
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email already registered"));
        }

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .verified(false)                               // not verified yet
                .verificationToken(verificationToken)
                .verificationTokenExpiry(LocalDateTime.now().plusHours(24))
                .build();

        userRepository.save(user);

        // Send verification email
        try {
            emailService.sendVerificationEmail(
                    user.getEmail(),
                    user.getName(),
                    verificationToken
            );
        } catch (Exception e) {
            System.err.println("Email send failed: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Registration successful! Please check your email to verify your account.",
                        "email", user.getEmail()
                ));
    }

    // Login — block if not verified
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

            // Block unverified users
            if (!user.isVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "message", "Please verify your email before logging in.",
                                "unverified", true
                        ));
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getId());

            return ResponseEntity.ok(new LoginResponse(
                    token, user.getId(), user.getName(), user.getEmail()
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
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
                        .verified(true)
                        .googleUser(true)
                        .build();
                return userRepository.save(newUser);
            });

            //Always verify existing unverified users who login with Google
            // Google already verified their email, so we trust it
            if (!user.isVerified()) {
                user.setVerified(true);
                user.setVerificationToken(null);
                user.setVerificationTokenExpiry(null);
                userRepository.save(user);
            }

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

    // Verify email — called when user clicks link
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid verification link"));
        }

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Verification link has expired. Please register again."));
        }

        // Mark as verified
        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        // Return JWT so user is auto-logged in after verification
        String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getId());

        return ResponseEntity.ok(new LoginResponse(
                jwtToken, user.getId(), user.getName(), user.getEmail()
        ));
    }

    // Resend verification email
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email is required"));
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Don't reveal if email exists — security best practice
            return ResponseEntity.ok(Map.of("message", "If this email is registered, a verification email will be sent."));
        }

        if (user.isVerified()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "This email is already verified. Please log in."));
        }

        // Generate new token
        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getName(), newToken);
            System.out.println("Verification email sent to: " + email);
        } catch (Exception e) {
            // Print link to console as fallback
            System.err.println("❌ Email send failed: " + e.getMessage());
            System.out.println("=== VERIFICATION LINK (use this for testing) ===");
            System.out.println("http://localhost:5173/verify-email?token=" + newToken);
            System.out.println("================================================");
        }

        return ResponseEntity.ok(Map.of(
                "message", "Verification email sent! Check your inbox."
        ));
    }
    // POST /auth/forgot-password — send reset email
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        User user = userRepository.findByEmail(email).orElse(null);

        // Always return success — don't reveal if email exists
        if (user == null) {
            return ResponseEntity.ok(Map.of(
                    "message", "If this email is registered, you will receive a reset link."
            ));
        }

        // Generate reset token (expires in 1 hour)
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), resetToken);
        } catch (Exception e) {
            System.err.println("Reset email failed: " + e.getMessage());
            // Still print to console for dev testing
            System.out.println("=== RESET LINK ===");
            System.out.println("http://localhost:5173/reset-password?token=" + resetToken);
            System.out.println("==================");
        }

        return ResponseEntity.ok(Map.of(
                "message", "If this email is registered, you will receive a reset link."
        ));
    }

    // ✅ POST /auth/reset-password — set new password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if (token == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Token and new password are required"));
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Password must be at least 6 characters"));
        }

        User user = userRepository.findByResetPasswordToken(token).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid or expired reset link"));
        }

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Reset link has expired. Please request a new one."));
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);

        // Send confirmation email
        try {
            emailService.sendPasswordChangedEmail(user.getEmail(), user.getName());
        } catch (Exception e) {
            System.err.println("Could not send confirmation email: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of("message", "Password reset successfully! You can now log in."));
    }
}