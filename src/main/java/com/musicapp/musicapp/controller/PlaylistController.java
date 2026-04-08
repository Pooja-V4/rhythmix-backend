package com.musicapp.musicapp.controller;

import com.musicapp.musicapp.entity.Playlist;
import com.musicapp.musicapp.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    // POST /playlists/{userId} → Create playlist for user
    @PostMapping("/{userId}")
    public ResponseEntity<Playlist> createPlaylist(@PathVariable Long userId,
                                                   @RequestBody Playlist playlist) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playlistService.createPlaylist(userId, playlist));
    }

    // GET /playlists/user/{userId} → Get all playlists for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Playlist>> getUserPlaylists(@PathVariable Long userId) {
        return ResponseEntity.ok(playlistService.getUserPlaylists(userId));
    }

    // POST /playlists/{playlistId}/songs/{songId} → Add song to playlist
    @PostMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Playlist> addSong(@PathVariable Long playlistId,
                                            @PathVariable Long songId) {
        return ResponseEntity.ok(playlistService.addSongToPlaylist(playlistId, songId));
    }

    // DELETE /playlists/{playlistId}/songs/{songId} → Remove song from playlist
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public ResponseEntity<Playlist> removeSong(@PathVariable Long playlistId,
                                               @PathVariable Long songId) {
        return ResponseEntity.ok(playlistService.removeSongFromPlaylist(playlistId, songId));
    }
}