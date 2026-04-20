package com.ivan.projects.movieplatform.controller;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.dto.response.WatchlistResponse;
import com.ivan.projects.movieplatform.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @Operation(summary = "Get all watchlist movie IDs")
    @GetMapping
    public List<Integer> getWatchlistIds(@AuthenticationPrincipal User user) {
        return watchlistService.getWatchlistIds(user);
    }

    @Operation(summary = "Get all watchlist movies with details")
    @GetMapping("/details")
    public List<WatchlistResponse> getWatchlistDetails(@AuthenticationPrincipal User user) {
        return watchlistService.getWatchlistDetails(user);
    }

    @Operation(summary = "Add a movie to the watchlist")
    @PostMapping("/{movieId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addToWatchlist(@AuthenticationPrincipal User user, @PathVariable Integer movieId) {
        watchlistService.addToWatchlist(user, movieId);
    }

    @Operation(summary = "Remove a movie from the watchlist")
    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromWatchlist(@AuthenticationPrincipal User user, @PathVariable Integer movieId) {
        watchlistService.removeFromWatchlist(user, movieId);
    }
}