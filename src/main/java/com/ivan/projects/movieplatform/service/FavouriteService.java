package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.Favourite;
import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.repository.FavouriteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavouriteService {

    private final FavouriteRepository favouriteRepository;

    public FavouriteService(FavouriteRepository favouriteRepository) {
        this.favouriteRepository = favouriteRepository;
    }

    public List<Integer> getFavourites(User user) {
        return favouriteRepository.findMovieIdsByUser(user);
    }

    @Transactional
    public void addFavourite(User user, Integer movieId) {
        if (favouriteRepository.existsByUserAndMovieId(user, movieId)) {
            return;
        }
        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setMovieId(movieId);
        favouriteRepository.save(favourite);
    }

    @Transactional
    public void removeFavourite(User user, Integer movieId) {
        favouriteRepository.deleteByUserAndMovieId(user, movieId);
    }
}