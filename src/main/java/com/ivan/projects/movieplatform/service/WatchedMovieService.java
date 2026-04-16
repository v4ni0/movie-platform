package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.WatchedMovie;
import com.ivan.projects.movieplatform.repository.WatchedMovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WatchedMovieService {

    private final WatchedMovieRepository watchedMovieRepository;

    public WatchedMovieService(WatchedMovieRepository watchedMovieRepository) {
        this.watchedMovieRepository = watchedMovieRepository;
    }

    public List<Integer> getWatchedMovies(User user) {
        return watchedMovieRepository.findMovieIdsByUser(user);
    }

    @Transactional
    public void addWatchedMovie(User user, Integer movieId) {
        watchedMovieRepository.findByUserAndMovieId(user, movieId).ifPresentOrElse(
            existing -> {
                existing.setWatchedAt(LocalDateTime.now());
                watchedMovieRepository.save(existing);
            },
            () -> {
                WatchedMovie watchedMovie = new WatchedMovie();
                watchedMovie.setUser(user);
                watchedMovie.setMovieId(movieId);
                watchedMovie.setWatchedAt(LocalDateTime.now());
                watchedMovieRepository.save(watchedMovie);
            }
        );
    }

    @Transactional
    public void removeWatchedMovie(User user, Integer movieId) {
        watchedMovieRepository.deleteByUserAndMovieId(user, movieId);
    }
}