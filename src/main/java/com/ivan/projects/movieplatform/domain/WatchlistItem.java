package com.ivan.projects.movieplatform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "watchlist_items", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"}))
@Getter
@Setter
@NoArgsConstructor
public class WatchlistItem {

    public WatchlistItem(User user, Integer movieId) {
        this.user = user;
        this.movieId = movieId;
        this.addedAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "movie_id")
    Integer movieId;

    @Column(name = "title")
    String title;

    @Column(name = "poster_path")
    String posterPath;

    @Column(name = "release_date")
    String releaseDate;

    @Column(name = "vote_average")
    Double voteAverage;

    @Column(name = "added_at")
    LocalDateTime addedAt;
}