package com.ivan.projects.movieplatform.dto.response;

import com.ivan.projects.movieplatform.domain.UserMovie;

import java.time.LocalDateTime;

public record UserMovieResponse(
    Integer movieId,
    String title,
    String posterPath,
    String releaseDate,
    Double voteAverage,
    boolean favourite,
    boolean onWatchlist,
    LocalDateTime addedAt
) {
    public static UserMovieResponse toResponse(UserMovie movie) {
        return new UserMovieResponse(
            movie.getMovieId(),
            movie.getTitle(),
            movie.getPosterPath(),
            movie.getReleaseDate(),
            movie.getVoteAverage(),
            movie.isFavourite(),
            movie.isOnWatchlist(),
            movie.getAddedAt()
        );
    }
}