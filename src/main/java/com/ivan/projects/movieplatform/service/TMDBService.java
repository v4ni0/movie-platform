package com.ivan.projects.movieplatform.service;

import com.google.gson.Gson;
import com.ivan.projects.movieplatform.domain.tmdb.Movie;
import com.ivan.projects.movieplatform.dto.MovieResponse;
import com.ivan.projects.movieplatform.dto.VideoResponse;
import com.ivan.projects.movieplatform.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TMDBService {
    private static final String MOVIE_ENDPOINT = "https://api.themoviedb.org/3/movie/";
    private static final String SEARCH_ENDPOINT = "https://api.themoviedb.org/3/search/movie";
    private static final String VIDEOS_ENDPOINT = "/videos";
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

    private URI buildVideoUri(Integer movieId) {
        return URI.create(MOVIE_ENDPOINT + movieId + VIDEOS_ENDPOINT + "?api_key=" + apiKey);
    }

    private URI buildSearchUri(String query) {
        String encodedQuery = URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
        return URI.create(SEARCH_ENDPOINT + "?query=" + encodedQuery + "&api_key=" + apiKey);
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

    public MovieResponse searchMovies(String query) throws IOException, InterruptedException {
        URI uri = buildSearchUri(query);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == GOOD_RESPONSE_CODE) {
            return gson.fromJson(response.body(), MovieResponse.class);
        } else {
            throw new IOException("Failed to fetch search results: " + response.body());
        }
    }

    public VideoResponse getMovieVideos(Integer movieId) throws IOException, InterruptedException {
        Validator.validatePositiveInteger(movieId, "Movie ID must be a positive integer.");
        URI uri = buildVideoUri(movieId);
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == GOOD_RESPONSE_CODE) {
            return gson.fromJson(response.body(), VideoResponse.class);
        } else {
            throw new IOException("Failed to fetch video data: " + response.body());
        }
    }

}
