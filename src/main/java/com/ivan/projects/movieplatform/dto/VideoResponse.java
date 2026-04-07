package com.ivan.projects.movieplatform.dto;

import java.util.List;

public record VideoResponse(
    Integer id,
    List<Video> results
) {
}

