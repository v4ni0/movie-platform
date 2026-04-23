package com.ivan.projects.movieplatform.exception;

public class MovieRecommendException extends RuntimeException {
    public MovieRecommendException(String message) {
        super(message);
    }

    public MovieRecommendException(String message, Throwable e) {
        super(message, e);
    }
}
