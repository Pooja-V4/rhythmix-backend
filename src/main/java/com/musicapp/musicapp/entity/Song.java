package com.musicapp.musicapp.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String artist;

    private String album;

    private Integer durationSeconds;

    @ManyToMany(mappedBy = "songs")
    @JsonIgnore
    private List<Playlist> playlists = new ArrayList<>();

    // ===== Constructors =====
    public Song() {}

    // ===== Getters =====
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public Integer getDurationSeconds() { return durationSeconds; }
    public List<Playlist> getPlaylists() { return playlists; }

    // ===== Setters =====
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setArtist(String artist) { this.artist = artist; }
    public void setAlbum(String album) { this.album = album; }
    public void setDurationSeconds(Integer d) { this.durationSeconds = d; }
    public void setPlaylists(List<Playlist> p) { this.playlists = p; }

    // ===== Builder =====
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String title;
        private String artist;
        private String album;
        private Integer durationSeconds;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String t) { this.title = t; return this; }
        public Builder artist(String a) { this.artist = a; return this; }
        public Builder album(String a) { this.album = a; return this; }
        public Builder durationSeconds(Integer d) { this.durationSeconds = d; return this; }

        public Song build() {
            Song s = new Song();
            s.id = this.id;
            s.title = this.title;
            s.artist = this.artist;
            s.album = this.album;
            s.durationSeconds = this.durationSeconds;
            return s;
        }
    }
}