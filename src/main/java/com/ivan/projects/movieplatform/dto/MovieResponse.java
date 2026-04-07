package com.ivan.projects.movieplatform.dto;

import com.google.gson.annotations.SerializedName;
import com.ivan.projects.movieplatform.domain.tmdb.Movie;

import java.util.List;

public record MovieResponse(
    Integer page,
    List<Movie> results,
    @SerializedName("total_pages")
    Integer totalPages,
    @SerializedName("total_results")
    Integer totalResults
) {
}
