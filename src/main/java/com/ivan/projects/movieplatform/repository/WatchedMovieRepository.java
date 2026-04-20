package com.ivan.projects.movieplatform.repository;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.WatchedMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WatchedMovieRepository extends JpaRepository<WatchedMovie, Long> {

    List<WatchedMovie> findAllByUser(User user);

    List<WatchedMovie> findTop5ByUserOrderByWatchedAtDesc(User user);

    @Query("SELECT w.movieId FROM WatchedMovie w WHERE w.user = :user ORDER BY w.watchedAt DESC")
    List<Integer> findMovieIdsByUser(@Param("user") User user);

    @Query("SELECT w FROM WatchedMovie w WHERE w.user = :user AND w.movieId = :movieId")
    Optional<WatchedMovie> findByUserAndMovieId(@Param("user") User user, @Param("movieId") Integer movieId);

    @Modifying
    @Query("DELETE FROM WatchedMovie w WHERE w.user = :user AND w.movieId = :movieId")
    void deleteByUserAndMovieId(@Param("user") User user, @Param("movieId") Integer movieId);
}