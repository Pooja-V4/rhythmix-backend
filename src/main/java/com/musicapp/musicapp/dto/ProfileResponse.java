package com.musicapp.musicapp.dto;

import java.time.LocalDateTime;

public class ProfileResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private int totalPlaylists;
    private int totalFavorites;
    private int totalSongs;

    public ProfileResponse() {}

    public ProfileResponse(Long id, String name, String email,
                           LocalDateTime createdAt, int totalPlaylists,
                           int totalFavorites, int totalSongs) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.totalPlaylists = totalPlaylists;
        this.totalFavorites = totalFavorites;
        this.totalSongs = totalSongs;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getTotalPlaylists() { return totalPlaylists; }
    public int getTotalFavorites() { return totalFavorites; }
    public int getTotalSongs() { return totalSongs; }
}