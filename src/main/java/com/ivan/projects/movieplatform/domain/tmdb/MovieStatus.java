package com.ivan.projects.movieplatform.domain.tmdb;

public enum MovieStatus {
    RUMORED("Rumored"),
    PLANNED("Planned"),
    IN_PRODUCTION("In Production"),
    POST_PRODUCTION("Post Production"),
    RELEASED("Released"),
    CANCELED("Canceled");

    private final String value;

    MovieStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
