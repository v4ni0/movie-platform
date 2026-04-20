package com.ivan.projects.movieplatform.dto.response;

import java.time.LocalDateTime;

public record FavouriteResponse(
    Integer movieId,
    String title,
    String posterPath,
    String releaseDate,
    Double voteAverage,
    LocalDateTime addedAt
) {}