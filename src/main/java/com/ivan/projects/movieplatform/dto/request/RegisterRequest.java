package com.ivan.projects.movieplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "username is required")
    @Size(min = 4, max = 50, message = "username must be 4-50 characters")
    String username,
    @NotBlank(message = "password is required")
    @Size(min = 8, max = 50, message = "password must be 8-50 characters")
    String password
) {}