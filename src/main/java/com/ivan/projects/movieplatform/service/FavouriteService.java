package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.Favourite;
import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.dto.response.FavouriteResponse;
import com.ivan.projects.movieplatform.repository.FavouriteRepository;
import com.ivan.projects.movieplatform.vo.Movie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FavouriteService {

    private final FavouriteRepository favouriteRepository;
    private final TMDBService tmdbService;

    public FavouriteService(FavouriteRepository favouriteRepository, TMDBService tmdbService) {
        this.favouriteRepository = favouriteRepository;
        this.tmdbService = tmdbService;
    }

    public List<Integer> getFavourites(User user) {
        return favouriteRepository.findMovieIdsByUser(user);
    }

    public List<FavouriteResponse> getFavouriteDetails(User user) {
        List<Favourite> favourites = favouriteRepository.findAllByUser(user);
        List<FavouriteResponse> responses = new ArrayList<>();
        for (Favourite fav : favourites) {
            String title = null;
            String posterPath = null;
            String releaseDate = null;
            Double voteAverage = null;
            try {
                Movie movie = tmdbService.getMovieById(fav.getMovieId());
                title = movie.title();
                posterPath = movie.posterPath();
                releaseDate = movie.releaseDate();
                voteAverage = movie.voteAverage();
            } catch (IOException e) {
                // partial response
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            responses.add(new FavouriteResponse(
                fav.getMovieId(), title, posterPath, releaseDate, voteAverage, fav.getAddedAt()
            ));
        }
        return responses;
    }

    @Transactional
    public void addFavourite(User user, Integer movieId) {
        if (favouriteRepository.existsByUserAndMovieId(user, movieId)) {
            return;
        }
        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setMovieId(movieId);
        favourite.setAddedAt(LocalDateTime.now());
        favouriteRepository.save(favourite);
    }

    @Transactional
    public void removeFavourite(User user, Integer movieId) {
        favouriteRepository.deleteByUserAndMovieId(user, movieId);
    }
}