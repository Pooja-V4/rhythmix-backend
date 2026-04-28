package com.musicapp.musicapp.service;

import com.musicapp.musicapp.entity.Playlist;
import com.musicapp.musicapp.entity.Song;
import com.musicapp.musicapp.entity.User;
import com.musicapp.musicapp.repository.PlaylistRepository;
import com.musicapp.musicapp.repository.SongRepository;
import com.musicapp.musicapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public PlaylistService(PlaylistRepository playlistRepository,
                           UserRepository userRepository,
                           SongRepository songRepository) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    // Create playlist for a user
    public Playlist createPlaylist(Long userId, Playlist playlist) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if user already has a playlist with same name
        if (playlistRepository.existsByUserIdAndNameIgnoreCase(userId, playlist.getName())) {
            throw new RuntimeException(
                    "You already have a playlist named \"" + playlist.getName() + "\""
            );
        }

        playlist.setUser(user);
        return playlistRepository.save(playlist);
    }

    // Get all playlists for a user
    public List<Playlist> getUserPlaylists(Long userId) {
        return playlistRepository.findByUserId(userId);
    }

    // Add a song to a playlist
    public Playlist addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        boolean alreadyExists = playlist.getSongs().stream()
                .anyMatch(s -> s.getId().equals(songId));

        if (alreadyExists) {
            throw new RuntimeException("Song already exists in this playlist");
        }

        playlist.getSongs().add(song);
        return playlistRepository.save(playlist);
    }

    // Remove a song from a playlist
    public Playlist removeSongFromPlaylist(Long playlistId, Long songId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        playlist.getSongs().removeIf(song -> song.getId().equals(songId));
        return playlistRepository.save(playlist);
    }
}