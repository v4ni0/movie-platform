package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.WatchlistItem;
import com.ivan.projects.movieplatform.dto.response.WatchlistResponse;
import com.ivan.projects.movieplatform.repository.WatchlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

    public WatchlistService(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public List<Integer> getWatchlistIds(User user) {
        return watchlistRepository.findMovieIdsByUser(user);
    }

    public List<WatchlistResponse> getWatchlistDetails(User user) {
        return watchlistRepository.findAllByUser(user).stream()
            .map(WatchlistResponse::toResponse)
            .toList();
    }

    @Transactional
    public void addToWatchlist(User user, Integer movieId) {
        if (!(watchlistRepository.findByUserAndMovieId(user, movieId)).isPresent()) {
            watchlistRepository.save(new WatchlistItem(user, movieId));
        }
    }

    @Transactional
    public void removeFromWatchlist(User user, Integer movieId) {
        watchlistRepository.deleteByUserAndMovieId(user, movieId);
    }
}