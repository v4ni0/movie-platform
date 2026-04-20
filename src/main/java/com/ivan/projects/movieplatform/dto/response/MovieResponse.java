package com.ivan.projects.movieplatform.dto.response;

import com.google.gson.annotations.SerializedName;
import com.ivan.projects.movieplatform.vo.Movie;

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
