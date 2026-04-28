package com.musicapp.musicapp.service;

import com.musicapp.musicapp.entity.Song;
import com.musicapp.musicapp.repository.SongRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SongService {

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }
    // Find existing song or create new one — prevents duplicate songs in DB
    public Song findOrCreate(Song song) {
        return songRepository
                .findByTitleIgnoreCaseAndArtistIgnoreCase(
                        song.getTitle(), song.getArtist()
                )
                .orElseGet(() -> songRepository.save(song));
    }
    public Song addSong(Song song) {
        return songRepository.save(song);
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Song getSongById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found with id: " + id));
    }

    public void deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new RuntimeException("Song not found with id: " + id);
        }
        songRepository.deleteById(id);
    }
}