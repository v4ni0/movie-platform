package com.ivan.projects.movieplatform.vo;

public record MovieUserStatus(
    boolean watched,
    Integer rating,
    String notes,
    boolean favourite,
    boolean onWatchlist
) {}