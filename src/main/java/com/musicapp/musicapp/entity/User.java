package com.musicapp.musicapp.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    // Email verification fields
    @Column(nullable = false)
    private boolean verified = false;

    @Column
    @JsonIgnore
    private String verificationToken;

    @Column
    private LocalDateTime verificationTokenExpiry;

    @Column
    @JsonIgnore
    private String resetPasswordToken;

    @Column
    private LocalDateTime resetPasswordTokenExpiry;

    @Column(nullable = false)
    private boolean googleUser = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Playlist> playlists = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Favorite> favorites = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ===== Constructors =====
    public User() {}

    // ===== Getters =====
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean isVerified() { return verified; }
    public String getVerificationToken() { return verificationToken; }
    public LocalDateTime getVerificationTokenExpiry() { return verificationTokenExpiry; }
    public String getResetPasswordToken() { return resetPasswordToken; }
    public LocalDateTime getResetPasswordTokenExpiry() { return resetPasswordTokenExpiry; }
    public boolean isGoogleUser() { return googleUser; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<Playlist> getPlaylists() { return playlists; }
    public List<Favorite> getFavorites() { return favorites; }

    // ===== Setters =====
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public void setVerificationToken(String token) { this.verificationToken = token; }
    public void setVerificationTokenExpiry(LocalDateTime expiry) { this.verificationTokenExpiry = expiry; }
    public void setResetPasswordToken(String token) { this.resetPasswordToken = token; }
    public void setResetPasswordTokenExpiry(LocalDateTime expiry) { this.resetPasswordTokenExpiry = expiry; }
    public void setGoogleUser(boolean googleUser) { this.googleUser = googleUser; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setPlaylists(List<Playlist> playlists) { this.playlists = playlists; }
    public void setFavorites(List<Favorite> favorites) { this.favorites = favorites; }

    // ===== Builder =====
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private String password;
        private boolean verified = false;
        private String verificationToken;
        private LocalDateTime verificationTokenExpiry;
        private String resetPasswordToken;
        private LocalDateTime resetPasswordTokenExpiry;
        private boolean googleUser = false;
        private List<Playlist> playlists = new ArrayList<>();
        private List<Favorite> favorites = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder verified(boolean v) { this.verified = v; return this; }
        public Builder verificationToken(String t) { this.verificationToken = t; return this; }
        public Builder verificationTokenExpiry(LocalDateTime e) { this.verificationTokenExpiry = e; return this; }
        public Builder resetPasswordToken(String t) { this.resetPasswordToken = t; return this; }
        public Builder resetPasswordTokenExpiry(LocalDateTime e) { this.resetPasswordTokenExpiry = e; return this; }
        public Builder googleUser(boolean g) { this.googleUser = g; return this; }
        public Builder playlists(List<Playlist> p) { this.playlists = p; return this; }
        public Builder favorites(List<Favorite> f) { this.favorites = f; return this; }

        public User build() {
            User u = new User();
            u.id = this.id;
            u.name = this.name;
            u.email = this.email;
            u.password = this.password;
            u.verified = this.verified;
            u.verificationToken = this.verificationToken;
            u.verificationTokenExpiry = this.verificationTokenExpiry;
            u.resetPasswordToken = this.resetPasswordToken;
            u.resetPasswordTokenExpiry = this.resetPasswordTokenExpiry;
            u.googleUser = this.googleUser;
            u.playlists = this.playlists;
            u.favorites = this.favorites;
            return u;
        }
    }
}