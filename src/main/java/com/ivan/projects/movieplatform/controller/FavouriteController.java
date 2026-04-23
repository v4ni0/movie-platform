package com.ivan.projects.movieplatform.controller;

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
@RequestMapping("/api/favourites")
public class FavouriteController {

    private final UserMovieService userMovieService;

    public FavouriteController(UserMovieService userMovieService) {
        this.userMovieService = userMovieService;
    }

    @Operation(summary = "Get all favourite movie IDs")
    @GetMapping
    public List<Integer> getFavourites(@AuthenticationPrincipal User user) {
        return userMovieService.getFavouriteIds(user);
    }

    @Operation(summary = "Get all favourites with details")
    @GetMapping("/details")
    public List<UserMovieResponse> getFavouriteDetails(@AuthenticationPrincipal User user) {
        return userMovieService.getFavouriteDetails(user);
    }

    @Operation(summary = "Add a movie to favourites")
    @PostMapping("/{movieId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addFavourite(@AuthenticationPrincipal User user, @PathVariable Integer movieId,
                             @Valid @RequestBody UserMovieRequest body) {
        userMovieService.addFavourite(user, movieId, body);
    }

    @Operation(summary = "Remove a movie from favourites")
    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavourite(@AuthenticationPrincipal User user, @PathVariable Integer movieId) {
        userMovieService.removeFavourite(user, movieId);
    }
}