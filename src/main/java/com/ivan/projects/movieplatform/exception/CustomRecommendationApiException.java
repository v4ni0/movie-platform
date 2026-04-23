package com.ivan.projects.movieplatform.exception;

public class CustomRecommendationApiException extends Exception{
    public CustomRecommendationApiException(String message) {
        super(message);
    }

    public CustomRecommendationApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
