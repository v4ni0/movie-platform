package com.ivan.projects.movieplatform.service.integration;

import com.ivan.projects.movieplatform.exception.TmdbFetchingException;
import com.ivan.projects.movieplatform.service.TMDBService;
import com.ivan.projects.movieplatform.vo.Movie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "TMDB_API_KEY", matches = ".+")
class TMDBServiceIntegrationTest {

    @Autowired
    private TMDBService tmdbService;

    @Test
    void testGetMovieByIdMeg2() throws TmdbFetchingException {
        Integer movieId = 615656;
        Movie movie = tmdbService.getMovieById(movieId);
        assertEquals(movieId, movie.id(), "Movie ID should match");
        assertEquals("Meg 2: The Trench", movie.title(), "Movie title should match");
    }

    @Test
    void testGetMovieByIdShazam() throws TmdbFetchingException {
        Integer movieId = 573435;
        String expected = "Bad Boys: Ride or Die";
        Movie movie = tmdbService.getMovieById(movieId);
        assertEquals(expected, movie.title(), "Movie title should match");
    }

    @Test
    void testGetMovieByIdTuttapposto() throws TmdbFetchingException {
        Integer movieId = 612501;
        String expected = "Tuttapposto";
        Movie movie = tmdbService.getMovieById(movieId);
        assertEquals(expected, movie.title(), "Movie title should match");
    }
}