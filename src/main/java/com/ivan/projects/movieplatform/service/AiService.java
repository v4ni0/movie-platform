package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.vo.Genre;
import com.ivan.projects.movieplatform.vo.Movie;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String generateMovieDescription(String title, Movie movie) {
        String prompt;
        if (movie == null) {
            prompt = "Write a short 2-3 sentence description for a movie titled \"" + title + "\".";
        } else {
            String genres = (movie.genres() != null && !movie.genres().isEmpty())
                ? movie.genres().stream()
                  .map(Genre::name)
                  .filter(name -> name != null && !name.isBlank())
                  .collect(Collectors.joining(", "))
                : "not specified";
            String year = movie.releaseDate() != null ? movie.releaseDate().substring(0, 4) : "unknown";
            prompt = String.format(
                "Based on the following details, write a compelling 2-3 sentence description for the movie \"%s\" (%s). " +
                "Genres: %s. Original overview: %s. " +
                "Make it engaging and suitable for a movie platform.",
                movie.title(), year, genres, movie.overview()
            );
        }
        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }

    public RecommendationResponse getRecommendationsFallback(String description, int numberOfRecommendations) {
        String prompt = """
            Based on this description: "%s", suggest exactly %d real movies.
            For each include the id (if known, else null),
            title, and a relevance score between 0 and 1.
            """.formatted(description, numberOfRecommendations);
        return chatClient.prompt()
            .user(prompt)
            .call()
            .entity(RecommendationResponse.class);
    }
}