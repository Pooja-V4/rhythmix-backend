package com.musicapp.musicapp.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "song_id"})
)
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"favorites", "playlists", "hibernateLazyInitializer"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    @JsonIgnoreProperties({"playlists", "hibernateLazyInitializer"})
    private Song song;

    // ===== Constructors =====
    public Favorite() {}

    public Favorite(User user, Song song) {
        this.user = user;
        this.song = song;
    }

    // ===== Getters =====
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Song getSong() { return song; }

    // ===== Setters =====
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setSong(Song song) { this.song = song; }

    // ===== Builder =====
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private User user;
        private Song song;

        public Builder user(User user) { this.user = user; return this; }
        public Builder song(Song song) { this.song = song; return this; }

        public Favorite build() {
            Favorite f = new Favorite();
            f.user = this.user;
            f.song = this.song;
            return f;
        }
    }
}