package com.musicapp.musicapp.service;

import com.musicapp.musicapp.entity.Favorite;
import com.musicapp.musicapp.entity.Song;
import com.musicapp.musicapp.entity.User;
import com.musicapp.musicapp.repository.FavoriteRepository;
import com.musicapp.musicapp.repository.SongRepository;
import com.musicapp.musicapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           UserRepository userRepository,
                           SongRepository songRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    public Favorite addFavorite(Long userId, Long songId) {
        if (favoriteRepository.existsByUserIdAndSongId(userId, songId)) {
            throw new RuntimeException("Song already in favorites");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        // use constructor instead of builder
        Favorite favorite = new Favorite(user, song);
        return favoriteRepository.save(favorite);
    }

    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Transactional
    public void removeFavorite(Long userId, Long songId) {
        if (!favoriteRepository.existsByUserIdAndSongId(userId, songId)) {
            throw new RuntimeException("Favorite not found");
        }
        favoriteRepository.deleteByUserIdAndSongId(userId, songId);
    }
}