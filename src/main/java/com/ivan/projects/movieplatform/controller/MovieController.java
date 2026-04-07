package com.ivan.projects.movieplatform.controller;

import com.ivan.projects.movieplatform.domain.tmdb.Movie;
import com.ivan.projects.movieplatform.dto.MovieResponse;
import com.ivan.projects.movieplatform.dto.Video;
import com.ivan.projects.movieplatform.dto.VideoResponse;
import com.ivan.projects.movieplatform.service.TMDBService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final TMDBService tmdbService;

    public MovieController(TMDBService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @Operation(summary = "Get a movie by its ID")
    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Integer id) throws IOException, InterruptedException {
        return tmdbService.getMovieById(id);
    }

    @Operation(summary = "Search for movies by title")
    @GetMapping("/search")
    public MovieResponse searchMovies(@RequestParam String query) throws IOException, InterruptedException {
        return tmdbService.searchMovies(query);
    }

    @Operation(summary = "Get trailers for a specific movie by its ID")
    @GetMapping("/{id}/trailers")
    public List<Video> getMovieTrailers(@PathVariable Integer id) throws IOException, InterruptedException {
        VideoResponse videoResponse = tmdbService.getMovieVideos(id);
        return videoResponse.results().stream()
            .filter(video -> "Trailer".equalsIgnoreCase(video.type()) && "YouTube".equalsIgnoreCase(video.site()))
            .collect(Collectors.toList());
    }
}
