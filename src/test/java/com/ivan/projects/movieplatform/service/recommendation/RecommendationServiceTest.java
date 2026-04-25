package com.ivan.projects.movieplatform.service.recommendation;

import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.exception.CustomRecommendationApiException;
import com.ivan.projects.movieplatform.exception.MovieRecommendException;
import com.ivan.projects.movieplatform.exception.TmdbFetchingException;
import com.ivan.projects.movieplatform.service.TMDBService;
import com.ivan.projects.movieplatform.vo.Movie;
import com.ivan.projects.movieplatform.vo.MovieRecommendation;
import com.ivan.projects.movieplatform.vo.MovieStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RecommendationServiceTest {

    private final TMDBService tmdbService = mock(TMDBService.class);
    private final RecommendationStrategy customApiStrategy = mock(RecommendationStrategy.class);
    private final RecommendationStrategy aiStrategy = mock(RecommendationStrategy.class);
    private final RecommendationService service =
        new RecommendationService(tmdbService, customApiStrategy, aiStrategy);

    private RecommendationResponse responseWith(Integer... ids) {
        List<MovieRecommendation> recs = java.util.Arrays.stream(ids)
            .map(id -> new MovieRecommendation(id, "Title " + id, 0.9))
            .toList();
        return new RecommendationResponse(recs);
    }

    private Movie movie(int id, String title) {
        return new Movie(id, "tt" + id, title, "overview", "2024-01-01",
            MovieStatus.RELEASED, "en", 120, 5000, 8.0, "/poster.jpg", List.of());
    }

    @Test
    void testCustomApiStrategyIsUsedWhenSpecified() throws Exception {
        when(customApiStrategy.recommend("sci-fi", 5)).thenReturn(responseWith(100, 200));
        when(tmdbService.getMovieById(100)).thenReturn(movie(100, "Inception"));
        when(tmdbService.getMovieById(200)).thenReturn(movie(200, "Interstellar"));

        List<Movie> result = service.getRecommendations("sci-fi", 5, RecommendationStrategyType.CUSTOM_API);

        assertEquals(2, result.size());
        assertEquals("Inception", result.get(0).title());
        assertEquals("Interstellar", result.get(1).title());
        verifyNoInteractions(aiStrategy);
    }

    @Test
    void testAiStrategyIsUsedWhenSpecified() throws Exception {
        when(aiStrategy.recommend("sci-fi", 5)).thenReturn(responseWith(300));
        when(tmdbService.getMovieById(300)).thenReturn(movie(300, "Dune"));

        List<Movie> result = service.getRecommendations("sci-fi", 5, RecommendationStrategyType.AI);

        assertEquals(1, result.size());
        assertEquals("Dune", result.get(0).title());
        verifyNoInteractions(customApiStrategy);
    }

    @Test
    void testAutoUsesCustomApiFirst() throws Exception {
        when(customApiStrategy.recommend("thriller", 3)).thenReturn(responseWith(100));
        when(tmdbService.getMovieById(100)).thenReturn(movie(100, "Inception"));

        List<Movie> result = service.getRecommendations("thriller", 3, RecommendationStrategyType.AUTO);

        assertEquals(1, result.size());
        verify(customApiStrategy).recommend("thriller", 3);
        verifyNoInteractions(aiStrategy);
    }

    @Test
    void testAutoFallsBackToAiWhenCustomApiFails() throws Exception {
        when(customApiStrategy.recommend(anyString(), anyInt()))
            .thenThrow(new CustomRecommendationApiException("API down"));
        when(aiStrategy.recommend("thriller", 3)).thenReturn(responseWith(200));
        when(tmdbService.getMovieById(200)).thenReturn(movie(200, "Interstellar"));

        List<Movie> result = service.getRecommendations("thriller", 3, RecommendationStrategyType.AUTO);

        assertEquals(1, result.size());
        assertEquals("Interstellar", result.get(0).title());
        verify(aiStrategy).recommend("thriller", 3);
    }

    @Test
    void testNullStrategyDefaultsToAuto() throws Exception {
        when(customApiStrategy.recommend("action", 5)).thenReturn(responseWith(100));
        when(tmdbService.getMovieById(100)).thenReturn(movie(100, "Mad Max"));

        service.getRecommendations("action", 5, null);

        verify(customApiStrategy).recommend("action", 5);
        verifyNoInteractions(aiStrategy);
    }

    @Test
    void testNullCountDefaultsToFive() throws Exception {
        when(customApiStrategy.recommend("action", 5)).thenReturn(responseWith(100));
        when(tmdbService.getMovieById(anyInt())).thenReturn(movie(1, "Test"));

        service.getRecommendations("action", null, RecommendationStrategyType.CUSTOM_API);

        verify(customApiStrategy).recommend("action", 5);
    }

    @Test
    void testZeroCountDefaultsToFive() throws Exception {
        when(customApiStrategy.recommend("action", 5)).thenReturn(responseWith(100));
        when(tmdbService.getMovieById(anyInt())).thenReturn(movie(1, "Test"));

        service.getRecommendations("action", 0, RecommendationStrategyType.CUSTOM_API);

        verify(customApiStrategy).recommend("action", 5);
    }

    @Test
    void testNullMovieIdsAreFiltered() throws Exception {
        RecommendationResponse response = new RecommendationResponse(List.of(
            new MovieRecommendation(null, "Unknown", 0.5),
            new MovieRecommendation(100, "Inception", 0.9)
        ));
        when(customApiStrategy.recommend("thriller", 5)).thenReturn(response);
        when(tmdbService.getMovieById(100)).thenReturn(movie(100, "Inception"));

        List<Movie> result = service.getRecommendations("thriller", 5, RecommendationStrategyType.CUSTOM_API);

        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).title());
    }

    @Test
    void testThrowsWhenTmdbFails() throws Exception {
        when(customApiStrategy.recommend("sci-fi", 5)).thenReturn(responseWith(100));
        when(tmdbService.getMovieById(100)).thenThrow(new TmdbFetchingException("TMDB down"));

        assertThrows(MovieRecommendException.class,
            () -> service.getRecommendations("sci-fi", 5, RecommendationStrategyType.CUSTOM_API));
    }

    @Test
    void testCustomApiStrategyFailureThrowsMovieRecommendException() throws Exception {
        when(customApiStrategy.recommend(anyString(), anyInt()))
            .thenThrow(new CustomRecommendationApiException("API down"));

        assertThrows(MovieRecommendException.class,
            () -> service.getRecommendations("sci-fi", 5, RecommendationStrategyType.CUSTOM_API));
        verifyNoInteractions(aiStrategy);
    }

    @Test
    void testAiStrategyFailureThrowsMovieRecommendException() throws Exception {
        when(aiStrategy.recommend(anyString(), anyInt()))
            .thenThrow(new CustomRecommendationApiException("AI down"));

        assertThrows(MovieRecommendException.class,
            () -> service.getRecommendations("sci-fi", 5, RecommendationStrategyType.AI));
        verifyNoInteractions(customApiStrategy);
    }

    @Test
    void testAutoBothStrategiesFailThrowsMovieRecommendException() throws Exception {
        when(customApiStrategy.recommend(anyString(), anyInt()))
            .thenThrow(new CustomRecommendationApiException("API down"));
        when(aiStrategy.recommend(anyString(), anyInt()))
            .thenThrow(new CustomRecommendationApiException("AI down"));

        assertThrows(MovieRecommendException.class,
            () -> service.getRecommendations("sci-fi", 5, RecommendationStrategyType.AUTO));
    }

    @Test
    void testNullRecommendationsInResponseThrows() throws Exception {
        when(customApiStrategy.recommend(anyString(), anyInt()))
            .thenReturn(new RecommendationResponse(null));

        assertThrows(MovieRecommendException.class,
            () -> service.getRecommendations("sci-fi", 5, RecommendationStrategyType.CUSTOM_API));
    }

    @Test
    void testNullResponseThrows() throws Exception {
        when(customApiStrategy.recommend(anyString(), anyInt())).thenReturn(null);

        assertThrows(MovieRecommendException.class,
            () -> service.getRecommendations("sci-fi", 5, RecommendationStrategyType.CUSTOM_API));
    }
}
