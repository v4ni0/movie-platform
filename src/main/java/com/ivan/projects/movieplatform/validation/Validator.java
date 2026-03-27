package com.ivan.projects.movieplatform.validation;

public class Validator {
    public static void validatePositiveInteger(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
