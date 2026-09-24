package com.example.campuslostfound.controller;

import com.example.campuslostfound.model.Favorite;
import com.example.campuslostfound.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity<Favorite> addFavorite(
            @RequestBody Favorite favorite) {

        return ResponseEntity.ok(
                favoriteService.addFavorite(favorite)
        );
    }

    @GetMapping
    public ResponseEntity<List<Favorite>> getAllFavorites() {
        return ResponseEntity.ok(
                favoriteService.getAllFavorites()
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favorite>> getFavoritesByUser(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                favoriteService.getFavoritesByUser(userId)
        );
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<Favorite>> getFavoritesByItem(
            @PathVariable Long itemId) {

        return ResponseEntity.ok(
                favoriteService.getFavoritesByItem(itemId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Favorite> getFavoriteById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                favoriteService.getFavoriteById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeFavorite(
            @PathVariable Long id) {

        favoriteService.removeFavorite(id);

        return ResponseEntity.ok("Favorite removed successfully");
    }
}