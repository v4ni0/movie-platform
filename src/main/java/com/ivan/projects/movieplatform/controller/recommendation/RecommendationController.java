package com.ivan.projects.movieplatform.controller.recommendation;

import com.ivan.projects.movieplatform.dto.request.RecommendationRequest;
import com.ivan.projects.movieplatform.service.recommendation.RecommendationService;
import com.ivan.projects.movieplatform.vo.Movie;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Operation(summary = "Get movie recommendations based on a description")
    @PostMapping
    public List<Movie> recommend(@Valid @RequestBody RecommendationRequest request) {
        return recommendationService.getRecommendations(request.description(), request.topK(), request.strategy());
    }
}