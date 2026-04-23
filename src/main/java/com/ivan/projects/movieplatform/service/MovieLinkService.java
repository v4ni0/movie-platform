package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.MovieLink;
import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.dto.request.MovieLinkRequest;
import com.ivan.projects.movieplatform.dto.response.MovieLinkResponse;
import com.ivan.projects.movieplatform.repository.MovieLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieLinkService {

    private final MovieLinkRepository movieLinkRepository;

    public MovieLinkService(MovieLinkRepository movieLinkRepository) {
        this.movieLinkRepository = movieLinkRepository;
    }

    public List<MovieLinkResponse> getAllLinks(User user) {
        return movieLinkRepository.findAllByUser(user).stream()
            .map(MovieLinkResponse::toResponse)
            .toList();
    }

    @Transactional
    public void addLink(User user, MovieLinkRequest request) {
        movieLinkRepository.save(new MovieLink(user, request.url(), request.rating()));
    }

    @Transactional
    public void removeLink(User user, Long linkId) {
        movieLinkRepository.deleteByIdAndUser(linkId, user);
    }
}