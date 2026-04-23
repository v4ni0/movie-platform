package com.ivan.projects.movieplatform.vo;

public record MovieRecommendation(
    Integer movieId,
    String title,
    Double score
) {}