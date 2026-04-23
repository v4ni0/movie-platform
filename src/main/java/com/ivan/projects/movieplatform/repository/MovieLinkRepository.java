package com.ivan.projects.movieplatform.repository;

import com.ivan.projects.movieplatform.domain.MovieLink;
import com.ivan.projects.movieplatform.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieLinkRepository extends JpaRepository<MovieLink, Long> {

    List<MovieLink> findAllByUser(User user);

    @Modifying
    @Query("DELETE FROM MovieLink ml WHERE ml.id = :id AND ml.user = :user")
    void deleteByIdAndUser(@Param("id") Long id, @Param("user") User user);
}