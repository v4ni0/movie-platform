package com.ivan.projects.movieplatform.dto.response;

import com.ivan.projects.movieplatform.domain.WatchedMovie;

import java.time.LocalDateTime;

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