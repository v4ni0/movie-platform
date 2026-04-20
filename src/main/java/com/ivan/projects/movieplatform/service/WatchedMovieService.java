package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.WatchedMovie;
import com.ivan.projects.movieplatform.dto.response.WatchedMovieResponse;
import com.ivan.projects.movieplatform.repository.WatchedMovieRepository;
import com.ivan.projects.movieplatform.vo.Movie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WatchedMovieService {

    private final WatchedMovieRepository watchedMovieRepository;
    private final TMDBService tmdbService;

    public WatchedMovieService(WatchedMovieRepository watchedMovieRepository, TMDBService tmdbService) {
        this.watchedMovieRepository = watchedMovieRepository;
        this.tmdbService = tmdbService;
    }

    public List<Integer> getWatchedMovies(User user) {
        return watchedMovieRepository.findMovieIdsByUser(user);
    }

    public List<WatchedMovieResponse> getWatchedMovieDetails(User user) {
        List<WatchedMovie> watchedMovies = watchedMovieRepository.findAllByUser(user);
        List<WatchedMovieResponse> responses = new ArrayList<>();
        for (WatchedMovie wm : watchedMovies) {
            responses.add(toResponse(wm));
        }
        return responses;
    }

    public List<WatchedMovieResponse> getRecentWatchedDetails(User user) {
        List<WatchedMovie> watchedMovies = watchedMovieRepository.findTop5ByUserOrderByWatchedAtDesc(user);
        List<WatchedMovieResponse> responses = new ArrayList<>();
        for (WatchedMovie wm : watchedMovies) {
            responses.add(toResponse(wm));
        }
        return responses;
    }

    private WatchedMovieResponse toResponse(WatchedMovie wm) {
        String title = null;
        String posterPath = null;
        String releaseDate = null;
        Double voteAverage = null;
        try {
            Movie movie = tmdbService.getMovieById(wm.getMovieId());
            title = movie.title();
            posterPath = movie.posterPath();
            releaseDate = movie.releaseDate();
            voteAverage = movie.voteAverage();
        } catch (IOException e) {
            // partial response with nulls for TMDB fields
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new WatchedMovieResponse(
            wm.getMovieId(), title, posterPath, releaseDate, voteAverage,
            wm.getRating(), wm.getNotes(), wm.getWatchedAt()
        );
    }

    @Transactional
    public void addWatchedMovie(User user, Integer movieId, Integer rating, String notes) {
        watchedMovieRepository.findByUserAndMovieId(user, movieId).ifPresentOrElse(
            existingMovie -> {
                existingMovie.setWatchedAt(LocalDateTime.now());
                existingMovie.setRating(rating);
                existingMovie.setNotes(notes);
                watchedMovieRepository.save(existingMovie);
            },
            () -> {
                WatchedMovie watchedMovie = new WatchedMovie(user, movieId, rating, notes);
                watchedMovieRepository.save(watchedMovie);
            }
        );
    }

    @Transactional
    public void removeWatchedMovie(User user, Integer movieId) {
        watchedMovieRepository.deleteByUserAndMovieId(user, movieId);
    }
}