package com.ivan.projects.movieplatform.controller.movie;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.dto.request.WatchedMovieRequest;
import com.ivan.projects.movieplatform.dto.response.WatchedMovieResponse;
import com.ivan.projects.movieplatform.service.WatchedMovieService;
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
@RequestMapping("/api/watched")
public class WatchedMovieController {

    private final WatchedMovieService watchedMovieService;

    public WatchedMovieController(WatchedMovieService watchedMovieService) {
        this.watchedMovieService = watchedMovieService;
    }

    @Operation(summary = "Get all watched movie IDs")
    @GetMapping
    public List<Integer> getWatchedMovies(@AuthenticationPrincipal User user) {
        return watchedMovieService.getWatchedMovies(user);
    }

    @Operation(summary = "Get all watched movies with details")
    @GetMapping("/details")
    public List<WatchedMovieResponse> getWatchedMovieDetails(@AuthenticationPrincipal User user) {
        return watchedMovieService.getWatchedMovieDetails(user);
    }

    @Operation(summary = "Mark a movie as watched")
    @PostMapping("/{movieId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addWatchedMovie(@AuthenticationPrincipal User user, @PathVariable Integer movieId,
                                @Valid @RequestBody WatchedMovieRequest body) {
        watchedMovieService.addWatchedMovie(user, movieId, body);
    }

    @Operation(summary = "Remove a movie from watched list")
    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeWatchedMovie(@AuthenticationPrincipal User user, @PathVariable Integer movieId) {
        watchedMovieService.removeWatchedMovie(user, movieId);
    }

    @Operation(summary = "Get the 5 most recently watched movies")
    @GetMapping("/recent")
    public List<WatchedMovieResponse> getRecentWatchedMovies(@AuthenticationPrincipal User user) {
        return watchedMovieService.getRecentWatchedDetails(user);
    }
}