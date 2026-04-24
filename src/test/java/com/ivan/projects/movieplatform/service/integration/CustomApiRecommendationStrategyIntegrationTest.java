package com.ivan.projects.movieplatform.service.integration;

import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.exception.CustomRecommendationApiException;
import com.ivan.projects.movieplatform.service.recommendation.CustomApiRecommendationStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CustomApiRecommendationStrategyIntegrationTest {

    @Autowired
    private CustomApiRecommendationStrategy strategy;

    @Test
    void testRecommendReturnsResults() throws CustomRecommendationApiException {
        RecommendationResponse result = strategy.recommend("action movie", 3);

        assertThat(result).isNotNull();
        assertThat(result.recommendations()).isNotNull();
        assertThat(result.recommendations()).isNotEmpty();
        result.recommendations()
            .forEach(movie -> {
                System.out.println("Movie ID: " + movie.id()
                    + ", Title: " + movie.title()
                    + ", Score: " + movie.score());
                assertThat(movie.title()).isNotBlank();
                assertThat(movie.score()).isBetween(0.0, 1.0);
            });
    }

    @Test
    void testRecommendRespectsTopK() throws CustomRecommendationApiException {
        int topK = 5;
        RecommendationResponse result = strategy.recommend("romantic comedy", topK);

        assertThat(result).isNotNull();
        assertThat(result.recommendations()).isNotNull();
        assertThat(result.recommendations()).hasSize(topK);
    }

    @Test
    void testRecommendWithSpecificGenre() throws CustomRecommendationApiException {
        RecommendationResponse result = strategy.recommend("sci-fi thriller set in space", 2);

        assertThat(result).isNotNull();
        assertThat(result.recommendations()).isNotNull();
        assertThat(result.recommendations()).hasSize(2);
    }
}
