package com.ivan.projects.movieplatform.dto.response;

import com.ivan.projects.movieplatform.domain.MovieLink;

import java.time.LocalDateTime;

// id is included so the frontend can target a specific link for DELETE /api/links/{id}.
// Without it, the frontend would have no way to identify which link the user clicked "remove" on.
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