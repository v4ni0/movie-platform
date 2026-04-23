package com.ivan.projects.movieplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserMovieRequest(

    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Poster path is required")
    String posterPath,

    @NotNull(message = "Release date is required")
    String releaseDate,

    @NotNull(message = "Vote average is required")
    Double voteAverage

) {}