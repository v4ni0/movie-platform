package com.ivan.projects.movieplatform.repository;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<WatchlistItem, Long> {

    List<WatchlistItem> findAllByUser(User user);

    List<WatchlistItem> findTop10ByUserOrderByAddedAtDesc(User user);

    @Query("SELECT w.movieId FROM WatchlistItem w WHERE w.user = :user ORDER BY w.addedAt DESC")
    List<Integer> findMovieIdsByUser(User user);

    Optional<WatchlistItem> findByUserAndMovieId(User user, Integer movieId);

    void deleteByUserAndMovieId(User user, Integer movieId);
}