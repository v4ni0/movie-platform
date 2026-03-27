package com.ivan.projects.movieplatform.service;

import com.google.gson.Gson;
import com.ivan.projects.movieplatform.domain.tmdb.Movie;
import com.ivan.projects.movieplatform.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TMDBService {
    private static final String MOVIE_ENDPOINT = "https://api.themoviedb.org/3/movie/";
    private static final Integer GOOD_RESPONSE_CODE = 200;

    @Value("${tmdb.api.key}")
    private String apiKey;

    private final HttpClient httpClient;
    private final Gson gson;

    public TMDBService(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.gson = new Gson();
    }

    private URI buildMovieUri(Integer movieId) {
        return URI.create(MOVIE_ENDPOINT + movieId + "?api_key=" + apiKey);
    }

    public Movie getMovieById(Integer movieId) throws IOException, InterruptedException {
        Validator.validatePositiveInteger(movieId, "Movie ID must be a positive integer.");
        URI uri = buildMovieUri(movieId);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == GOOD_RESPONSE_CODE) {
            return gson.fromJson(response.body(), Movie.class);
        } else {
            throw new IOException("Failed to fetch movie data: " + response.body());
        }

    }

}
