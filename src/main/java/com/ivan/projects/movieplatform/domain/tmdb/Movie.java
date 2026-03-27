package com.ivan.projects.movieplatform.domain.tmdb;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record Movie(
    Integer id,
    @SerializedName("imdb_id")
    String imdbId,
    String title,
    String overview,
    @SerializedName("release_date")
    String releaseDate,
    MovieStatus status,
    @SerializedName("original_language")
    String originalLanguage,
    Integer runtime,
    @SerializedName("vote_count")
    Integer voteCount,
    @SerializedName("vote_average")
    Double voteAverage,
    @SerializedName("poster_path")
    String posterPath,
    List<Genre> genres
) {
}
