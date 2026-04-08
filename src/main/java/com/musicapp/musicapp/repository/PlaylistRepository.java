package com.musicapp.musicapp.repository;

import com.musicapp.musicapp.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    // Get all playlists belonging to a specific user
    List<Playlist> findByUserId(Long userId);
}