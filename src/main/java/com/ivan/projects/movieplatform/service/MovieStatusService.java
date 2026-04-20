package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.WatchedMovie;
import com.ivan.projects.movieplatform.vo.MovieUserStatus;
import com.ivan.projects.movieplatform.repository.FavouriteRepository;
import com.ivan.projects.movieplatform.repository.WatchedMovieRepository;
import com.ivan.projects.movieplatform.repository.WatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MovieStatusService {

    private final WatchedMovieRepository watchedMovieRepository;
    private final FavouriteRepository favouriteRepository;
    private final WatchlistRepository watchlistRepository;

    public MovieStatusService(WatchedMovieRepository watchedMovieRepository,
                              FavouriteRepository favouriteRepository,
                              WatchlistRepository watchlistRepository) {
        this.watchedMovieRepository = watchedMovieRepository;
        this.favouriteRepository = favouriteRepository;
        this.watchlistRepository = watchlistRepository;
    }

    public MovieUserStatus getStatus(User user, Integer movieId) {
        Optional<WatchedMovie> watched = watchedMovieRepository.findByUserAndMovieId(user, movieId);
        boolean isFavourite = favouriteRepository.findByUserAndMovieId(user, movieId).isPresent();
        boolean isOnWatchlist = watchlistRepository.findByUserAndMovieId(user, movieId).isPresent();

        if (watched.isPresent()) {
            WatchedMovie movie = watched.get();
            return new MovieUserStatus(true, movie.getRating(), movie.getNotes(), isFavourite, isOnWatchlist);
        }
        return new MovieUserStatus(false, null, null, isFavourite, isOnWatchlist);
    }
}