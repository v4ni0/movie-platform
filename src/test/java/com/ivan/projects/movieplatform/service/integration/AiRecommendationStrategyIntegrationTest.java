package com.ivan.projects.movieplatform.service.integration;

import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.service.recommendation.AiRecommendationStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AiRecommendationStrategyIntegrationTest {

    @Autowired
    private AiRecommendationStrategy strategy;

    @Test
    void testRecommendRealRecommendationRequest() {
        RecommendationResponse result = strategy.recommend("action movie", 2);
        assertThat(result).isNotNull();
        assertThat(result.recommendations()).isNotNull();
        result.recommendations()
            .forEach(movie -> System.out.println(
                "Movie ID: " + movie.id() + ", Title: " + movie.title() + ", Score: " + movie.score()
            ));
    }
}
