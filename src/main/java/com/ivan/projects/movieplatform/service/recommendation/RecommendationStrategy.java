package com.ivan.projects.movieplatform.service.recommendation;

import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.exception.CustomRecommendationApiException;

public interface RecommendationStrategy {
    public RecommendationResponse recommend(String description, int count) throws CustomRecommendationApiException;
}
