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
@Table(name = "watched_movies", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"}))
@Getter
@Setter
@NoArgsConstructor
public class WatchedMovie {

    public WatchedMovie (User user, Integer movieId, String title, String posterPath, Integer rating, String notes) {
        this.user = user;
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.watchedAt = LocalDateTime.now();
        this.rating = rating;
        this.notes = notes;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "movie_id", nullable = false)
    private Integer movieId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "watched_at", nullable = false)
    private LocalDateTime watchedAt;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "notes", length = 2000)
    private String notes;

}
