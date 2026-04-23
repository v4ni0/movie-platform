package com.ivan.projects.movieplatform.dto.response;

import com.ivan.projects.movieplatform.domain.WatchedMovie;

import java.time.LocalDateTime;

// Fields reflect exactly what the WatchedMovie entity stores.
// releaseDate and voteAverage are intentionally omitted — the entity does not store them.
// The frontend can call GET /api/movies/{id} to get full TMDB details if needed.
public record WatchedMovieResponse(
    Integer movieId,
    String title,
    String posterPath,
    Integer rating,
    String notes,
    LocalDateTime watchedAt
) {
    public static WatchedMovieResponse toResponse(WatchedMovie movie) {
        return new WatchedMovieResponse(
            movie.getMovieId(),
            movie.getTitle(),
            movie.getPosterPath(),
            movie.getRating(),
            movie.getNotes(),
            movie.getWatchedAt()
        );
    }
}