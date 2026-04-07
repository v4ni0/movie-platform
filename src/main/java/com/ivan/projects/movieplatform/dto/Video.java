package com.ivan.projects.movieplatform.dto;

import com.google.gson.annotations.SerializedName;

public record Video(
    String key,
    String id,
    String name,
    String site,
    String type,
    boolean official,
    @SerializedName("published_at")
    String publishedAt
) {
}

