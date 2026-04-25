package com.ivan.projects.movieplatform.exception;

public class TmdbFetchingException extends Exception {
    public TmdbFetchingException(String message) {
        super(message);
    }

    public TmdbFetchingException(String message, Throwable e) {
        super(message, e);
    }
}
