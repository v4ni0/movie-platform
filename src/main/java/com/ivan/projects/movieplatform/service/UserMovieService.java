package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.UserMovie;
import com.ivan.projects.movieplatform.domain.WatchedMovie;
import com.ivan.projects.movieplatform.dto.request.UserMovieRequest;
import com.ivan.projects.movieplatform.dto.response.UserMovieResponse;
import com.ivan.projects.movieplatform.repository.UserMovieRepository;
import com.ivan.projects.movieplatform.repository.WatchedMovieRepository;
import com.ivan.projects.movieplatform.vo.MovieUserStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserMovieService {

    private final WatchedMovieRepository watchedMovieRepository;
    private final UserMovieRepository userMovieRepository;

    public UserMovieService(UserMovieRepository userMovieRepository, WatchedMovieRepository watchedMovieRepository) {
        this.watchedMovieRepository = watchedMovieRepository;
        this.userMovieRepository = userMovieRepository;
    }

    public MovieUserStatus getStatus(User user, Integer movieId) {
        Optional<WatchedMovie> watched = watchedMovieRepository.findByUserAndMovieId(user, movieId);
        Optional<UserMovie> userMovie = userMovieRepository.findByUserAndMovieId(user, movieId);

        boolean isWatched = watched.isPresent();
        boolean isFavourite = userMovie.map(UserMovie::isFavourite).orElse(false);
        boolean isOnWatchlist = userMovie.map(UserMovie::isOnWatchlist).orElse(false);

        return new MovieUserStatus(isWatched, isFavourite, isOnWatchlist);
    }

    // favourites

    public List<Integer> getFavouriteIds(User user) {
        return userMovieRepository.findFavouriteMovieIdsByUser(user);
    }

    public List<UserMovieResponse> getFavouriteDetails(User user) {
        return userMovieRepository.findAllByUserAndFavouriteTrue(user).stream()
            .map(UserMovieResponse::toResponse)
            .toList();
    }

    @Transactional
    public void addFavourite(User user, Integer movieId, UserMovieRequest request) {
        UserMovie movie = userMovieRepository.findByUserAndMovieId(user, movieId)
            .orElse(new UserMovie(user, movieId, request.title(), request.posterPath(), request.releaseDate(), request.voteAverage()));
        movie.setFavourite(true);
        userMovieRepository.save(movie);
    }

    @Transactional
    public void removeFavourite(User user, Integer movieId) {
        userMovieRepository.findByUserAndMovieId(user, movieId).ifPresent(movie -> {
            movie.setFavourite(false);
            userMovieRepository.save(movie);
        });
    }

    // watchlist
    public List<Integer> getWatchlistIds(User user) {
        return userMovieRepository.findWatchlistMovieIdsByUser(user);
    }

    public List<UserMovieResponse> getWatchlistDetails(User user) {
        return userMovieRepository.findAllByUserAndOnWatchlistTrue(user).stream()
            .map(UserMovieResponse::toResponse)
            .toList();
    }

    @Transactional
    public void addToWatchlist(User user, Integer movieId, UserMovieRequest request) {
        UserMovie movie = userMovieRepository.findByUserAndMovieId(user, movieId)
            .orElse(new UserMovie(user,
                movieId,
                request.title(),
                request.posterPath(),
                request.releaseDate(),
                request.voteAverage()));
        movie.setOnWatchlist(true);
        userMovieRepository.save(movie);
    }

    @Transactional
    public void removeFromWatchlist(User user, Integer movieId) {
        userMovieRepository.findByUserAndMovieId(user, movieId).ifPresent(movie -> {
            movie.setOnWatchlist(false);
            userMovieRepository.save(movie);
        });
    }
}