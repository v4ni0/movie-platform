package com.ivan.projects.movieplatform.dto.response;

import com.ivan.projects.movieplatform.vo.Video;

import java.util.List;

public record VideoResponse(
    Integer id,
    List<Video> results
) {
}

