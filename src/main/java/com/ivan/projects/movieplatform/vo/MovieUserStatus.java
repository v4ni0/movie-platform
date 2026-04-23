package com.ivan.projects.movieplatform.vo;

public record MovieUserStatus(
    boolean watched,
    boolean favourite,
    boolean onWatchlist
) {}