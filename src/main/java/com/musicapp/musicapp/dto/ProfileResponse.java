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
    private boolean googleUser;

    public ProfileResponse() {}

    public ProfileResponse(Long id, String name, String email,
                           LocalDateTime createdAt, int totalPlaylists,
                           int totalFavorites, int totalSongs, boolean googleUser) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.totalPlaylists = totalPlaylists;
        this.totalFavorites = totalFavorites;
        this.totalSongs = totalSongs;
        this.googleUser = googleUser;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public boolean isGoogleUser() { return googleUser; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public int getTotalPlaylists() { return totalPlaylists; }
    public int getTotalFavorites() { return totalFavorites; }
    public int getTotalSongs() { return totalSongs; }
}