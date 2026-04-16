package com.ivan.projects.movieplatform.repository;

import com.ivan.projects.movieplatform.domain.Favourite;
import com.ivan.projects.movieplatform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    @Query("SELECT f.movieId FROM Favourite f WHERE f.user = :user")
    List<Integer> findMovieIdsByUser(@Param("user") User user);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favourite f WHERE f.user = :user AND f.movieId = :movieId")
    boolean existsByUserAndMovieId(@Param("user") User user, @Param("movieId") Integer movieId);

    @Modifying
    @Query("DELETE FROM Favourite f WHERE f.user = :user AND f.movieId = :movieId")
    void deleteByUserAndMovieId(@Param("user") User user, @Param("movieId") Integer movieId);
}
