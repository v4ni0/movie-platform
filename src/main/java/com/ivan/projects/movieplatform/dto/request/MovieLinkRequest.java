package com.ivan.projects.movieplatform.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MovieLinkRequest (
    @NotBlank(message = "URL must not be blank")
    @Size(max = 2000, message = "URL must not exceed 2000 characters")
    String url,

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must be at most 10")
    Integer rating

) {}
