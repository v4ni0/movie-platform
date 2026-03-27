package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.tmdb.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TMDBServiceIntegrationTest {

    @Autowired
    private TMDBService tmdbService;

    @Test
    void testGetMovieByIdMeg2() throws IOException, InterruptedException {
        Integer movieId = 615656;
        Movie movie = tmdbService.getMovieById(movieId);
        assertEquals(movieId, movie.id(), "Movie ID should match");
        assertEquals("Meg 2: The Trench", movie.title(), "Movie title should match");
    }

    @Test
    void testGetMovieByIdShazam() throws IOException, InterruptedException {
        Integer movieId = 573435;
        String expected = "Bad Boys: Ride or Die";
        Movie movie = tmdbService.getMovieById(movieId);
        assertEquals(expected, movie.title(), "Movie title should match");
    }

    @Test
    void testGetMovieByIdTuttapposto() throws IOException, InterruptedException {
        Integer movieId = 612501;
        String expected = "Tuttapposto";
        Movie movie = tmdbService.getMovieById(movieId);
        assertEquals(expected, movie.title(), "Movie title should match");
    }
}