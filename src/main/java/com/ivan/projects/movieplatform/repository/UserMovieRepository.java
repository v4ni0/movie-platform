package com.ivan.projects.movieplatform.repository;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.UserMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserMovieRepository extends JpaRepository<UserMovie, Long> {

    Optional<UserMovie> findByUserAndMovieId(User user, Integer movieId);

    List<UserMovie> findAllByUserAndFavouriteTrue(User user);

    List<UserMovie> findAllByUserAndOnWatchlistTrue(User user);

    @Query("SELECT u.movieId FROM UserMovie u WHERE u.user = :user AND u.favourite = true ORDER BY u.addedAt DESC")
    List<Integer> findFavouriteMovieIdsByUser(@Param("user") User user);

    @Query("SELECT u.movieId FROM UserMovie u WHERE u.user = :user AND u.onWatchlist = true ORDER BY u.addedAt DESC")
    List<Integer> findWatchlistMovieIdsByUser(@Param("user") User user);
}