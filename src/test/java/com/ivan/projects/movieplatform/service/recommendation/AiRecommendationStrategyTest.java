package com.ivan.projects.movieplatform.service.recommendation;

import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.vo.MovieRecommendation;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AiRecommendationStrategyTest {

    private final ChatClient chatClient = mock(ChatClient.class);
    private final ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class);
    private final AiRecommendationStrategy strategy;

    AiRecommendationStrategyTest() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        strategy = new AiRecommendationStrategy(chatClientBuilder);
    }

    private CallResponseSpec mockChatChain() {
        ChatClientRequestSpec requestSpec = mock(ChatClientRequestSpec.class);
        CallResponseSpec callResponse = mock(CallResponseSpec.class);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponse);
        return callResponse;
    }

    @Test
    void testReturnsRecommendationResponseFromChatClient() {
        CallResponseSpec callResponse = mockChatChain();
        RecommendationResponse expected = new RecommendationResponse(
            List.of(new MovieRecommendation(100, "Inception", 0.95))
        );
        when(callResponse.entity(RecommendationResponse.class)).thenReturn(expected);

        RecommendationResponse result = strategy.recommend("sci-fi thriller", 3);

        assertNotNull(result);
        assertEquals(1, result.recommendations().size());
        assertEquals("Inception", result.recommendations().getFirst().title());
        assertEquals(100, result.recommendations().getFirst().id());
    }

    @Test
    void testMultipleRecommendationsAreReturned() {
        CallResponseSpec callResponse = mockChatChain();
        RecommendationResponse expected = new RecommendationResponse(List.of(
            new MovieRecommendation(100, "Inception", 0.95),
            new MovieRecommendation(200, "Interstellar", 0.90),
            new MovieRecommendation(300, "Dune", 0.85)
        ));
        when(callResponse.entity(RecommendationResponse.class)).thenReturn(expected);

        RecommendationResponse result = strategy.recommend("epic sci-fi", 3);

        assertEquals(3, result.recommendations().size());
        assertEquals("Dune", result.recommendations().get(2).title());
    }

    @Test
    void testPromptContainsDescriptionAndCount() {
        ChatClientRequestSpec requestSpec = mock(ChatClientRequestSpec.class);
        CallResponseSpec callResponse = mock(CallResponseSpec.class);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponse);
        when(callResponse.entity(RecommendationResponse.class))
            .thenReturn(new RecommendationResponse(List.of()));

        strategy.recommend("space adventure", 7);

        verify(requestSpec).user(argThat((String prompt) ->
            prompt.contains("space adventure") && prompt.contains("7")
        ));
    }

    @Test
    void testNullResponseFromChatClientIsPassedThrough() {
        CallResponseSpec callResponse = mockChatChain();
        when(callResponse.entity(RecommendationResponse.class)).thenReturn(null);

        RecommendationResponse result = strategy.recommend("drama", 5);

        assertNull(result);
    }
}
