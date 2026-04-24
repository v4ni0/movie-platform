package com.ivan.projects.movieplatform.controller.movie;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.dto.request.UserMovieRequest;
import com.ivan.projects.movieplatform.dto.response.UserMovieResponse;
import com.ivan.projects.movieplatform.service.UserMovieService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final UserMovieService userMovieService;

    public WatchlistController(UserMovieService userMovieService) {
        this.userMovieService = userMovieService;
    }

    @Operation(summary = "Get all watchlist movie IDs")
    @GetMapping
    public List<Integer> getWatchlistIds(@AuthenticationPrincipal User user) {
        return userMovieService.getWatchlistIds(user);
    }

    @Operation(summary = "Get all watchlist movies with details")
    @GetMapping("/details")
    public List<UserMovieResponse> getWatchlistDetails(@AuthenticationPrincipal User user) {
        return userMovieService.getWatchlistDetails(user);
    }

    @Operation(summary = "Add a movie to the watchlist")
    @PostMapping("/{movieId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addToWatchlist(@AuthenticationPrincipal User user, @PathVariable Integer movieId,
                               @Valid @RequestBody UserMovieRequest body) {
        userMovieService.addToWatchlist(user, movieId, body);
    }

    @Operation(summary = "Remove a movie from the watchlist")
    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromWatchlist(@AuthenticationPrincipal User user, @PathVariable Integer movieId) {
        userMovieService.removeFromWatchlist(user, movieId);
    }
}