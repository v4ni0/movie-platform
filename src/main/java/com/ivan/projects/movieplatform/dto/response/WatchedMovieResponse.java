package com.ivan.projects.movieplatform.dto.response;

import java.time.LocalDateTime;

public record WatchedMovieResponse(
    Integer movieId,
    String title,
    String posterPath,
    String releaseDate,
    Double voteAverage,
    Integer rating,
    String notes,
    LocalDateTime watchedAt
) {}