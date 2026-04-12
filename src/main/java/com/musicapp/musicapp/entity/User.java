package com.musicapp.musicapp.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Playlist> playlists = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Favorite> favorites = new ArrayList<>();

    // ===== Constructors =====
    public User() {}

    public User(Long id, String name, String email, String password,
                List<Playlist> playlists, List<Favorite> favorites) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.playlists = playlists;
        this.favorites = favorites;
    }

    // ===== Getters =====
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<Playlist> getPlaylists() { return playlists; }
    public List<Favorite> getFavorites() { return favorites; }

    // ===== Setters =====
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPlaylists(List<Playlist> playlists) { this.playlists = playlists; }
    public void setFavorites(List<Favorite> favorites) { this.favorites = favorites; }

    // ===== Builder =====
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private String password;
        private List<Playlist> playlists = new ArrayList<>();
        private List<Favorite> favorites = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder playlists(List<Playlist> p) { this.playlists = p; return this; }
        public Builder favorites(List<Favorite> f) { this.favorites = f; return this; }

        public User build() {
            User u = new User();
            u.id = this.id;
            u.name = this.name;
            u.email = this.email;
            u.password = this.password;
            u.playlists = this.playlists;
            u.favorites = this.favorites;
            return u;
        }
    }
}