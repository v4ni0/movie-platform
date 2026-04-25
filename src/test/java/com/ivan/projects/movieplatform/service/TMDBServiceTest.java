package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.dto.response.MovieResponse;
import com.ivan.projects.movieplatform.dto.response.VideoResponse;
import com.ivan.projects.movieplatform.exception.TmdbFetchingException;
import com.ivan.projects.movieplatform.vo.Movie;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TMDBServiceTest {

    private HttpClient httpClient = mock(HttpClient.class);
    private TMDBService tmdbService = new TMDBService(httpClient);

    private String movieJson = """
        {"id":615656,"title":"Meg 2: The Trench","overview":"An exploratory dive...",
         "release_date":"2023-08-04","vote_average":6.2,"poster_path":"/poster.jpg",
         "genres":[{"id":28,"name":"Action"}]}
        """;

    private String searchJson = """
        {"page":1,"results":[{"id":615656,"title":"Meg 2: The Trench","overview":"dive",
         "release_date":"2023-08-04","vote_average":6.2,"poster_path":"/poster.jpg",
         "genres":[]}],
         "total_pages":1,"total_results":1}
        """;

    private String videoJson = """
        {"id":615656,"results":[{"key":"abc123","id":"vid1","name":"Official Trailer",
         "site":"YouTube","type":"Trailer","official":true,"published_at":"2023-06-01"}]}
        """;

    @SuppressWarnings("unchecked")
    private void mockResponse(int statusCode, String body) throws IOException, InterruptedException {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);
    }

    @Test
    void testGetMovieByIdSuccess() throws TmdbFetchingException, IOException, InterruptedException {
        mockResponse(200, movieJson);

        Movie movie = tmdbService.getMovieById(615656);

        assertEquals(615656, movie.id(), "Movie ID should match");
        assertEquals("Meg 2: The Trench", movie.title(), "Title should match");
        assertEquals(1, movie.genres().size(), "Should have 1 genre");
    }

    @Test
    void testGetMovieByIdNonOkStatusThrows() throws IOException, InterruptedException {
        mockResponse(404, "Not Found");

        assertThrows(TmdbFetchingException.class, () -> tmdbService.getMovieById(999999),
            "Should throw TmdbFetchingException on non-200 response");
    }

    @Test
    void testGetMovieByIdNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> tmdbService.getMovieById(null),
            "Should throw IllegalArgumentException for null ID");
    }

    @Test
    void testGetMovieByIdNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () -> tmdbService.getMovieById(-1),
            "Should throw IllegalArgumentException for negative ID");
    }

    @Test
    void testGetMovieByIdZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> tmdbService.getMovieById(0),
            "Should throw IllegalArgumentException for zero ID");
    }

    @Test
    void testSearchMoviesSuccess() throws TmdbFetchingException, IOException, InterruptedException {
        mockResponse(200, searchJson);

        MovieResponse result = tmdbService.searchMovies("Meg");

        assertEquals(1, result.page(), "Page should be 1");
        assertEquals(1, result.results().size(), "Should return 1 result");
        assertEquals("Meg 2: The Trench", result.results().get(0).title(), "Title should match");
    }

    @Test
    void testSearchMoviesNonOkStatusThrows() throws IOException, InterruptedException {
        mockResponse(500, "Server Error");

        assertThrows(TmdbFetchingException.class, () -> tmdbService.searchMovies("Meg"),
            "Should throw TmdbFetchingException on non-200 response");
    }

    @Test
    void testGetMovieVideosSuccess() throws IOException, InterruptedException, TmdbFetchingException {
        mockResponse(200, videoJson);

        VideoResponse result = tmdbService.getMovieVideos(615656);

        assertEquals(1, result.results().size(), "Should return 1 video");
        assertEquals("abc123", result.results().get(0).key(), "Video key should match");
        assertEquals("Trailer", result.results().get(0).type(), "Type should be Trailer");
    }

    @Test
    void testGetMovieVideosNonOkStatusThrows() throws IOException, InterruptedException {
        mockResponse(404, "Not Found");

        assertThrows(TmdbFetchingException.class, () -> tmdbService.getMovieVideos(615656),
            "Should throw TmdbFetchingException on non-200 response");
    }

    @Test
    void testGetMovieVideosNullIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> tmdbService.getMovieVideos(null),
            "Should throw IllegalArgumentException for null ID");
    }

    @Test
    void testGetMovieByIdInterruptedExceptionThrows() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new InterruptedException("interrupted"));

        assertThrows(TmdbFetchingException.class, () -> tmdbService.getMovieById(1),
            "Should throw TmdbFetchingException when HTTP client is interrupted");
    }
}