package com.musicapp.musicapp.controller;

import com.musicapp.musicapp.entity.Song;
import com.musicapp.musicapp.service.SongService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    // POST /songs → Add a song
    @PostMapping
    public ResponseEntity<Song> addSong(@RequestBody Song song) {
        // findOrCreate prevents duplicate songs in DB
        Song saved = songService.addSong(song);
        return ResponseEntity.status(HttpStatus.OK).body(saved);
    }

    // GET /songs → Get all songs
    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        return ResponseEntity.ok(songService.getAllSongs());
    }

    // GET /songs/{id} → Get one song
    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    // DELETE /songs/{id} → Delete a song
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return ResponseEntity.ok("Song deleted successfully");
    }
}