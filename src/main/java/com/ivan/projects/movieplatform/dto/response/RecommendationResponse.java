package com.ivan.projects.movieplatform.dto.response;

import com.ivan.projects.movieplatform.vo.MovieRecommendation;

import java.util.List;

public record RecommendationResponse(List<MovieRecommendation> recommendations) {
}