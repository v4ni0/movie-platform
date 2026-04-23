package com.ivan.projects.movieplatform.service;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.exception.CustomRecommendationApiException;
import com.ivan.projects.movieplatform.exception.MovieRecommendException;
import com.ivan.projects.movieplatform.vo.Movie;
import com.ivan.projects.movieplatform.vo.MovieRecommendation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
public class RecommendationService {

    private static final String CUSTOM_RECOMMENDER_URL = "https://ivan0403-movie-recommender-api.hf.space/recommend";
    private static final int DEFAULT_NUMBER_OF_RECOMMENDATIONS = 5;

    private final TMDBService tmdbService;
    private final HttpClient httpClient;
    private final AiService aiService;
    private final Gson gson;

    public RecommendationService(TMDBService tmdbService, HttpClient httpClient, AiService aiService, Gson gson) {
        this.tmdbService = tmdbService;
        this.httpClient = httpClient;
        this.aiService = aiService;
        this.gson = gson;
    }

    private URI buildRecommendationUri(String description, Integer numberOfRecommendations) {
        String encoded = URLEncoder.encode(description, StandardCharsets.UTF_8);
        return URI.create(CUSTOM_RECOMMENDER_URL
            + "?description=" + encoded
            + "&number_of_recommendations=" + numberOfRecommendations);
    }

    private RecommendationResponse fetchFromCustomApi(String description, int numberOfRecommendations)
        throws CustomRecommendationApiException {
        URI uri = buildRecommendationUri(description, numberOfRecommendations);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), RecommendationResponse.class);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new CustomRecommendationApiException("Invalid response from recommender: " + e.getMessage());
        } catch (IOException e) {
            throw new CustomRecommendationApiException("Failed to reach recommender: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomRecommendationApiException("Failed to reach recommender: " + e.getMessage());
        }
    }

    public List<Movie> getRecommendations(String description, Integer numberOfRecommendations) {
        int number = (numberOfRecommendations != null && numberOfRecommendations > 0)
            ? numberOfRecommendations : DEFAULT_NUMBER_OF_RECOMMENDATIONS;

        RecommendationResponse response;
        try {
            response = fetchFromCustomApi(description, number);
        } catch (CustomRecommendationApiException e) {
            response = aiService.getRecommendationsFallback(description, number);
        }

        return response.recommendations().stream()
            .map(MovieRecommendation::movieId)
            .filter(Objects::nonNull)
            .map(id -> {
                try {
                    return tmdbService.getMovieById(id);
                } catch (IOException | InterruptedException e) {
                    throw new MovieRecommendException("Failed to fetch movie " + id + ": " + e.getMessage());
                }
            })
            .toList();
    }
}