package com.ivan.projects.movieplatform.service.recommendation;

import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.exception.CustomRecommendationApiException;
import com.ivan.projects.movieplatform.exception.MovieRecommendException;
import com.ivan.projects.movieplatform.service.TMDBService;
import com.ivan.projects.movieplatform.vo.Movie;
import com.ivan.projects.movieplatform.vo.MovieRecommendation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class RecommendationService {

    private static final int DEFAULT_NUMBER_OF_RECOMMENDATIONS = 5;

    private final TMDBService tmdbService;
    private final RecommendationStrategy customApiStrategy;
    private final RecommendationStrategy aiFallbackStrategy;

    public RecommendationService(
        TMDBService tmdbService,
        @Qualifier("customApiRecommendationStrategy") RecommendationStrategy customApiStrategy,
        @Qualifier("aiRecommendationStrategy") RecommendationStrategy aiFallbackStrategy
    ) {
        this.tmdbService = tmdbService;
        this.customApiStrategy = customApiStrategy;
        this.aiFallbackStrategy = aiFallbackStrategy;
    }

    public List<Movie> getRecommendations(String description, Integer count, RecommendationStrategyType strategy) {
        int number = (count != null && count > 0) ? count : DEFAULT_NUMBER_OF_RECOMMENDATIONS;
        RecommendationStrategyType resolved = (strategy != null) ? strategy : RecommendationStrategyType.AUTO;

        RecommendationResponse response = switch (resolved) {
            case CUSTOM_API -> {
                try {
                    yield customApiStrategy.recommend(description, number);
                } catch (CustomRecommendationApiException e) {
                    throw new MovieRecommendException("Custom API recommendation failed", e);
                }
            }
            case AI -> {
                try {
                    yield aiFallbackStrategy.recommend(description, number);
                } catch (CustomRecommendationApiException e) {
                    throw new MovieRecommendException("AI recommendation failed", e);
                }
            }
            case AUTO -> {
                try {
                    yield customApiStrategy.recommend(description, number);
                } catch (CustomRecommendationApiException e) {
                    try {
                        yield aiFallbackStrategy.recommend(description, number);
                    } catch (CustomRecommendationApiException ex) {
                        throw new MovieRecommendException("All recommendation strategies failed", ex);
                    }
                }
            }
        };

        if (response == null || response.recommendations() == null) {
            throw new MovieRecommendException("Recommendation response is empty");
        }

        return response.recommendations().stream()
            .map(MovieRecommendation::id)
            .filter(Objects::nonNull)
            .map(this::fetchMovieById)
            .toList();
    }

    private Movie fetchMovieById(Integer id) {
        try {
            return tmdbService.getMovieById(id);
        } catch (IOException | InterruptedException e) {
            throw new MovieRecommendException("Failed to fetch movie " + id, e);
        }
    }
}
