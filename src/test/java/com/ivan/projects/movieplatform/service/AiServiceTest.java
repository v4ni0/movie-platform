package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.vo.Genre;
import com.ivan.projects.movieplatform.vo.Movie;
import com.ivan.projects.movieplatform.vo.MovieRecommendation;
import com.ivan.projects.movieplatform.vo.MovieStatus;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AiServiceTest {

    private final ChatClient chatClient = mock(ChatClient.class);
    private final ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class);

    private AiService createService() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        return new AiService(chatClientBuilder);
    }

    private void mockChatResponse(String content) {
        ChatClientRequestSpec requestSpec = mock(ChatClientRequestSpec.class);
        CallResponseSpec callResponse = mock(CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponse);
        when(callResponse.content()).thenReturn(content);
    }

    @Test
    void testGenerateMovieDescriptionWithMovie() {
        AiService aiService = createService();
        mockChatResponse("A thrilling action movie.");

        Movie movie = new Movie(1, "tt123", "Inception", "A mind-bending thriller",
            "2010-07-16", MovieStatus.RELEASED, "en", 148, 30000, 8.8, "/poster.jpg",
            List.of(new Genre(28, "Action")));

        String result = aiService.generateMovieDescription("Inception", movie);

        assertEquals("A thrilling action movie.", result, "Should return AI-generated description");
    }

    @Test
    void testGenerateMovieDescriptionWithNullMovie() {
        AiService aiService = createService();
        mockChatResponse("A movie description for an unknown title.");

        String result = aiService.generateMovieDescription("Unknown Movie", null);

        assertEquals("A movie description for an unknown title.", result,
            "Should return description even when movie is null");
    }

    @Test
    void testGenerateMovieDescriptionWithNullReleaseDate() {
        AiService aiService = createService();
        mockChatResponse("Description with unknown year.");

        Movie movie = new Movie(1, "tt123", "Test Movie", "overview",
            null, MovieStatus.RELEASED, "en", 120, 1000, 7.0, "/poster.jpg",
            List.of(new Genre(18, "Drama")));

        String result = aiService.generateMovieDescription("Test Movie", movie);

        assertEquals("Description with unknown year.", result,
            "Should handle null release date gracefully");
    }

    @Test
    void testGenerateMovieDescriptionWithEmptyGenres() {
        AiService aiService = createService();
        mockChatResponse("Description with no genres.");

        Movie movie = new Movie(1, "tt123", "Test Movie", "overview",
            "2020-01-01", MovieStatus.RELEASED, "en", 120, 1000, 7.0, "/poster.jpg",
            List.of());

        String result = aiService.generateMovieDescription("Test Movie", movie);

        assertEquals("Description with no genres.", result,
            "Should handle empty genres gracefully");
    }

    @Test
    void testGetRecommendationsFallback() {
        AiService aiService = createService();

        ChatClientRequestSpec requestSpec = mock(ChatClientRequestSpec.class);
        CallResponseSpec callResponse = mock(CallResponseSpec.class);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponse);

        RecommendationResponse expected = new RecommendationResponse(
            List.of(new MovieRecommendation(100, "Interstellar", 0.95))
        );
        when(callResponse.entity(RecommendationResponse.class)).thenReturn(expected);

        RecommendationResponse result = aiService.getRecommendationsFallback("sci-fi thriller", 3);

        assertEquals(1, result.recommendations().size(), "Should return 1 recommendation");
        assertEquals("Interstellar", result.recommendations().get(0).title(), "Title should match");
    }
}