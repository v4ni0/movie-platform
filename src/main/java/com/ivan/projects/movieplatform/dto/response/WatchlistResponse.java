package com.ivan.projects.movieplatform.dto.response;

import com.ivan.projects.movieplatform.domain.WatchlistItem;

import java.time.LocalDateTime;

public record WatchlistResponse(
    Integer movieId,
    String title,
    String posterPath,
    String releaseDate,
    Double voteAverage,
    LocalDateTime addedAt
) {
    public static WatchlistResponse toResponse(WatchlistItem item) {
        return new WatchlistResponse(
            item.getMovieId(),
            item.getTitle(),
            item.getPosterPath(),
            item.getReleaseDate(),
            item.getVoteAverage(),
            item.getAddedAt()
        );
    }
}