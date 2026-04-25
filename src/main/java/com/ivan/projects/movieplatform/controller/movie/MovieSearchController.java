package com.ivan.projects.movieplatform.controller.movie;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.exception.TmdbFetchingException;
import com.ivan.projects.movieplatform.service.UserMovieService;
import com.ivan.projects.movieplatform.vo.MovieUserStatus;
import com.ivan.projects.movieplatform.vo.Movie;
import com.ivan.projects.movieplatform.dto.response.MovieResponse;
import com.ivan.projects.movieplatform.vo.Video;
import com.ivan.projects.movieplatform.dto.response.VideoResponse;
import com.ivan.projects.movieplatform.dto.response.MovieDescriptionResponse;
import com.ivan.projects.movieplatform.service.TMDBService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieSearchController {

    private final TMDBService tmdbService;
    private final UserMovieService userMovieService;

    public MovieSearchController(TMDBService tmdbService, UserMovieService userMovieService) {
        this.tmdbService = tmdbService;
        this.userMovieService = userMovieService;
    }

    @Operation(summary = "Get a movie by its ID")
    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Integer id) throws TmdbFetchingException {
        return tmdbService.getMovieById(id);
    }

    @Operation(summary = "Search for movies by title")
    @GetMapping("/search")
    public MovieResponse searchMovies(@RequestParam String query) throws TmdbFetchingException {
        return tmdbService.searchMovies(query);
    }

    @Operation(summary = "Get trailers for a specific movie by its ID")
    @GetMapping("/{id}/trailers")
    public List<Video> getMovieTrailers(@PathVariable Integer id) throws TmdbFetchingException {
        VideoResponse videoResponse = tmdbService.getMovieVideos(id);
        return videoResponse.results().stream()
            .filter(video -> "Trailer".equalsIgnoreCase(video.type()) && "YouTube".equalsIgnoreCase(video.site()))
            .toList();
    }

    @Operation(summary = "Get user status for a movie (watched/favourite/watchlist)")
    @GetMapping("/{id}/status")
    public MovieUserStatus getStatus(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        return userMovieService.getStatus(user, id);
    }

    @Operation(summary = "Get an AI-generated description for a movie by title")
    @GetMapping("/{id}/description")
    public MovieDescriptionResponse getDescriptionByTitle(@PathVariable Integer id) throws TmdbFetchingException {
        Movie movie = tmdbService.getMovieById(id);
        return userMovieService.generateMovieDescription(movie);
    }
}
