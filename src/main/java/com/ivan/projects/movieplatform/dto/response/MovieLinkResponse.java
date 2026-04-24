package com.ivan.projects.movieplatform.dto.response;

import com.ivan.projects.movieplatform.domain.MovieLink;

import java.time.LocalDateTime;

public record MovieLinkResponse(
    Long id,
    String url,
    Integer rating,
    LocalDateTime addedAt
) {
    public static MovieLinkResponse toResponse(MovieLink link) {
        return new MovieLinkResponse(link.getId(), link.getUrl(), link.getRating(), link.getAddedAt());
    }
}