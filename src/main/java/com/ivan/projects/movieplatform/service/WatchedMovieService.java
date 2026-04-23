package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.WatchedMovie;
import com.ivan.projects.movieplatform.dto.request.WatchedMovieRequest;
import com.ivan.projects.movieplatform.dto.response.WatchedMovieResponse;
import com.ivan.projects.movieplatform.repository.WatchedMovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<WatchedMovieResponse> getWatchedMovieDetails(User user) {
        return watchedMovieRepository.findAllByUser(user).stream()
            .map(WatchedMovieResponse::toResponse)
            .toList();
    }

    public List<WatchedMovieResponse> getRecentWatchedDetails(User user) {
        return watchedMovieRepository.findTop5ByUserOrderByWatchedAtDesc(user).stream()
            .map(WatchedMovieResponse::toResponse)
            .toList();
    }

    @Transactional
    public void addWatchedMovie(User user, Integer movieId, WatchedMovieRequest request) {
        if (watchedMovieRepository.findByUserAndMovieId(user, movieId).isEmpty()) {
            watchedMovieRepository.save(new WatchedMovie(user, movieId, request.title(), request.posterPath(), request.rating(), request.notes()));
        }
    }

    @Transactional
    public void removeWatchedMovie(User user, Integer movieId) {
        watchedMovieRepository.deleteByUserAndMovieId(user, movieId);
    }
}