package com.ivan.projects.movieplatform.service;

import com.google.gson.Gson;
import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.exception.MovieRecommendException;
import com.ivan.projects.movieplatform.vo.Movie;
import com.ivan.projects.movieplatform.vo.MovieRecommendation;
import com.ivan.projects.movieplatform.vo.MovieStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    private final TMDBService tmdbService = mock(TMDBService.class);
    private final HttpClient httpClient = mock(HttpClient.class);
    private final AiService aiService = mock(AiService.class);
    private final Gson gson = new Gson();
    private final RecommendationService recommendationService =
        new RecommendationService(tmdbService, httpClient, aiService, gson);

    private String customApiResponse = """
        {"recommendations":[{"movieId":100,"title":"Inception","score":0.95},
                             {"movieId":200,"title":"Interstellar","score":0.90}]}
        """;

    private Movie createMovie(int id, String title) {
        return new Movie(id, "tt" + id, title, "overview", "2024-01-01",
            MovieStatus.RELEASED, "en", 120, 5000, 8.0, "/poster.jpg", List.of());
    }

    @SuppressWarnings("unchecked")
    private void mockHttpResponse(int statusCode, String body) throws IOException, InterruptedException {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);
    }

    @Test
    void testGetRecommendationsFromCustomApi() throws IOException, InterruptedException {
        mockHttpResponse(200, customApiResponse);
        when(tmdbService.getMovieById(100)).thenReturn(createMovie(100, "Inception"));
        when(tmdbService.getMovieById(200)).thenReturn(createMovie(200, "Interstellar"));

        List<Movie> result = recommendationService.getRecommendations("sci-fi thriller", 5);

        assertEquals(2, result.size(), "Should return 2 recommendations");
        assertEquals("Inception", result.get(0).title(), "First title should match");
        assertEquals("Interstellar", result.get(1).title(), "Second title should match");
    }

    @Test
    void testGetRecommendationsDefaultsTo5WhenNull() throws IOException, InterruptedException {
        mockHttpResponse(200, customApiResponse);
        when(tmdbService.getMovieById(anyInt())).thenReturn(createMovie(1, "Test"));

        recommendationService.getRecommendations("action movie", null);

        verify(httpClient).send(
            argThat(req -> req.uri().toString().contains("number_of_recommendations=5")),
            any()
        );
    }

    @Test
    void testGetRecommendationsDefaultsTo5WhenZero() throws IOException, InterruptedException {
        mockHttpResponse(200, customApiResponse);
        when(tmdbService.getMovieById(anyInt())).thenReturn(createMovie(1, "Test"));

        recommendationService.getRecommendations("action movie", 0);

        verify(httpClient).send(
            argThat(req -> req.uri().toString().contains("number_of_recommendations=5")),
            any()
        );
    }

    @Test
    void testGetRecommendationsFallsBackToAiOnHttpError() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new IOException("Connection refused"));
        RecommendationResponse fallback = new RecommendationResponse(
            List.of(new MovieRecommendation(300, "Dune", 0.85))
        );
        when(aiService.getRecommendationsFallback(anyString(), anyInt())).thenReturn(fallback);
        when(tmdbService.getMovieById(300)).thenReturn(createMovie(300, "Dune"));

        List<Movie> result = recommendationService.getRecommendations("desert sci-fi", 3);

        assertEquals(1, result.size(), "Should return 1 fallback recommendation");
        assertEquals("Dune", result.get(0).title(), "Should use AI fallback result");
        verify(aiService).getRecommendationsFallback("desert sci-fi", 3);
    }

    @Test
    void testGetRecommendationsFallsBackToAiOnInvalidJson() throws IOException, InterruptedException {
        mockHttpResponse(200, "not valid json at all");
        RecommendationResponse fallback = new RecommendationResponse(
            List.of(new MovieRecommendation(400, "Matrix", 0.9))
        );
        when(aiService.getRecommendationsFallback(anyString(), anyInt())).thenReturn(fallback);
        when(tmdbService.getMovieById(400)).thenReturn(createMovie(400, "Matrix"));

        List<Movie> result = recommendationService.getRecommendations("action", 5);

        assertEquals(1, result.size(), "Should return AI fallback result on invalid JSON");
    }

    @Test
    void testGetRecommendationsFiltersNullMovieIds() throws IOException, InterruptedException {
        String responseWithNull = """
            {"recommendations":[{"movieId":null,"title":"Unknown","score":0.5},
                                 {"movieId":100,"title":"Inception","score":0.9}]}
            """;
        mockHttpResponse(200, responseWithNull);
        when(tmdbService.getMovieById(100)).thenReturn(createMovie(100, "Inception"));

        List<Movie> result = recommendationService.getRecommendations("thriller", 5);

        assertEquals(1, result.size(), "Should filter out null movie IDs");
        assertEquals("Inception", result.get(0).title(), "Should only contain valid movie");
    }

    @Test
    void testGetRecommendationsThrowsWhenTmdbFails() throws IOException, InterruptedException {
        mockHttpResponse(200, customApiResponse);
        when(tmdbService.getMovieById(100)).thenThrow(new IOException("TMDB down"));

        assertThrows(MovieRecommendException.class,
            () -> recommendationService.getRecommendations("sci-fi", 5),
            "Should throw MovieRecommendException when TMDB fetch fails");
    }

    @Test
    void testGetRecommendationsFallsBackOnInterruptedException() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new InterruptedException("interrupted"));
        RecommendationResponse fallback = new RecommendationResponse(
            List.of(new MovieRecommendation(500, "Arrival", 0.88))
        );
        when(aiService.getRecommendationsFallback(anyString(), anyInt())).thenReturn(fallback);
        when(tmdbService.getMovieById(500)).thenReturn(createMovie(500, "Arrival"));

        List<Movie> result = recommendationService.getRecommendations("alien contact", 5);

        assertEquals(1, result.size(), "Should fall back to AI on interrupted exception");
        assertEquals("Arrival", result.get(0).title(), "Should return fallback result");
    }
}