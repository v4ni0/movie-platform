package com.ivan.projects.movieplatform.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "movie_links")
@Getter
@Setter
@NoArgsConstructor
public class MovieLink {
    public MovieLink(User user, String url, Integer rating) {
        this.user = user;
        this.url = url;
        this.rating = rating;
        this.addedAt = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "url", nullable = false, length = 2000)
    private String url;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @Column(name = "rating")
    private Integer rating;
}