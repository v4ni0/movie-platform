package com.ivan.projects.movieplatform.controller.movie;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.dto.request.MovieLinkRequest;
import com.ivan.projects.movieplatform.dto.response.MovieLinkResponse;
import com.ivan.projects.movieplatform.service.MovieLinkService;
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
@RequestMapping("/api/links")
public class MovieLinkController {

    private final MovieLinkService movieLinkService;

    public MovieLinkController(MovieLinkService movieLinkService) {
        this.movieLinkService = movieLinkService;
    }

    @Operation(summary = "Get all personal links")
    @GetMapping
    public List<MovieLinkResponse> getAllLinks(@AuthenticationPrincipal User user) {
        return movieLinkService.getAllLinks(user);
    }

    @Operation(summary = "Add a personal link for a movie")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addLink(@AuthenticationPrincipal User user, @RequestBody @Valid MovieLinkRequest request) {
        movieLinkService.addLink(user, request);
    }

    @Operation(summary = "Remove a personal link")
    @DeleteMapping("/{linkId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLink(@AuthenticationPrincipal User user,
                           @PathVariable Long linkId) {
        movieLinkService.removeLink(user, linkId);
    }
}