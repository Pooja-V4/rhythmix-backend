package com.musicapp.musicapp.controller;

import com.musicapp.musicapp.entity.Favorite;
import com.musicapp.musicapp.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // POST /favorites/{userId}/songs/{songId} → Add to favorites
    @PostMapping("/{userId}/songs/{songId}")
    public ResponseEntity<Favorite> addFavorite(@PathVariable Long userId,
                                                @PathVariable Long songId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriteService.addFavorite(userId, songId));
    }

    // GET /favorites/{userId} → Get user's favorites
    @GetMapping("/{userId}")
    public ResponseEntity<List<Favorite>> getUserFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(userId));
    }

    // DELETE /favorites/{userId}/songs/{songId} → Remove from favorites
    @DeleteMapping("/{userId}/songs/{songId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long userId,
                                                 @PathVariable Long songId) {
        favoriteService.removeFavorite(userId, songId);
        return ResponseEntity.ok("Removed from favorites");
    }
}