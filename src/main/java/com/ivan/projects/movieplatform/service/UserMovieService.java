package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.UserMovie;
import com.ivan.projects.movieplatform.domain.WatchedMovie;
import com.ivan.projects.movieplatform.dto.request.UserMovieRequest;
import com.ivan.projects.movieplatform.dto.response.MovieDescriptionResponse;
import com.ivan.projects.movieplatform.dto.response.UserMovieResponse;
import com.ivan.projects.movieplatform.repository.UserMovieRepository;
import com.ivan.projects.movieplatform.repository.WatchedMovieRepository;
import com.ivan.projects.movieplatform.vo.Genre;
import com.ivan.projects.movieplatform.vo.Movie;
import com.ivan.projects.movieplatform.vo.MovieUserStatus;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserMovieService {

    private final ChatClient chatClient;
    private final WatchedMovieRepository watchedMovieRepository;
    private final UserMovieRepository userMovieRepository;

    public UserMovieService(UserMovieRepository userMovieRepository, WatchedMovieRepository watchedMovieRepository, ChatClient.Builder chatClientBuilder) {
        this.watchedMovieRepository = watchedMovieRepository;
        this.userMovieRepository = userMovieRepository;
        this.chatClient = chatClientBuilder.build();
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

    private String filterGenres(Movie movie) {
        if (movie.genres() == null || movie.genres().isEmpty()) {
            return "not specified";
        }
        return movie.genres().stream()
              .map(Genre::name)
              .filter(name -> name != null && !name.isBlank())
              .collect(Collectors.joining(", "));
    }

    public MovieDescriptionResponse generateMovieDescription(Movie movie) {
        String genres = filterGenres(movie);
        String year = movie.releaseDate() != null ? movie.releaseDate().substring(0, 4) : "unknown";

        String prompt = String.format(
            "Based on the following details, generate a JSON response for the movie \"%s\" (%s). " +
                "Genres: %s. Original overview: %s. " +
                "Return a JSON object with two fields: " +
                "\"description\" (a compelling 3-4 sentence summary suitable for a movie platform) and " +
                "\"suitableFor\" (a brief audience suitability note, e.g. \"All ages\", \"Teens and adults\", \"Adults only\").",
            movie.title(), year, genres, movie.overview()
        );

        return chatClient.prompt()
            .user(prompt)
            .call()
            .entity(MovieDescriptionResponse.class);
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