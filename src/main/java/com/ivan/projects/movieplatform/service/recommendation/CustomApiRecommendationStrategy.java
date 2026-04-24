package com.ivan.projects.movieplatform.service.recommendation;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.exception.CustomRecommendationApiException;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class CustomApiRecommendationStrategy implements RecommendationStrategy {

    private static final String CUSTOM_RECOMMENDER_URL = "https://ivan0403-movie-recommender-api.hf.space/recommend";

    private final HttpClient httpClient;
    private final Gson gson;

    public CustomApiRecommendationStrategy(HttpClient httpClient, Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
    }

    private URI buildRecommendationUri(String description, Integer numberOfRecommendations) {
        String encoded = URLEncoder.encode(description, StandardCharsets.UTF_8);
        return URI.create(CUSTOM_RECOMMENDER_URL
            + "?description=" + encoded
            + "&top_k=" + numberOfRecommendations);
    }

    @Override
    public RecommendationResponse recommend(String description, int count) throws CustomRecommendationApiException {
        URI uri = buildRecommendationUri(description, count);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new CustomRecommendationApiException("Recommender returned status " + response.statusCode());
            }
            return gson.fromJson(response.body(), RecommendationResponse.class);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new CustomRecommendationApiException("Invalid response from recommender: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomRecommendationApiException("Failed to reach recommender: " + e.getMessage());
        } catch (Exception e) {
            throw new CustomRecommendationApiException("Failed to reach recommender: " + e.getMessage());
        }
    }
}
