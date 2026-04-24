package com.ivan.projects.movieplatform.vo;

public record MovieRecommendation(
    Integer id,
    String title,
    Double score
) {}