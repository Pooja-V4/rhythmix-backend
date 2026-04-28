package com.musicapp.musicapp.repository;

import com.musicapp.musicapp.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    // Search songs by title (Phase 6 - search feature)
    List<Song> findByTitleContainingIgnoreCase(String title);

    // Search songs by artist
    List<Song> findByArtistContainingIgnoreCase(String artist);

    // Find exact song by title + artist to prevent duplicates
    Optional<Song> findByTitleIgnoreCaseAndArtistIgnoreCase(String title, String artist);
}