package com.ivan.projects.movieplatform.service.recommendation;

import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class AiRecommendationStrategy implements RecommendationStrategy {
    private final ChatClient chatClient;

    public AiRecommendationStrategy(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public RecommendationResponse recommend(String description, int count) {
        String prompt = """
            Based on this description: "%s", suggest exactly %d real movies that match the description.
            """.formatted(description, count);
        return chatClient.prompt()
            .user(prompt)
            .call()
            .entity(RecommendationResponse.class);
    }
}
