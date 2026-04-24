package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.WatchedMovie;
import com.ivan.projects.movieplatform.dto.request.WatchedMovieRequest;
import com.ivan.projects.movieplatform.dto.response.WatchedMovieResponse;
import com.ivan.projects.movieplatform.repository.WatchedMovieRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class WatchedMovieServiceTest {

    private final WatchedMovieRepository watchedMovieRepository = mock(WatchedMovieRepository.class);
    private final WatchedMovieService watchedMovieService = new WatchedMovieService(watchedMovieRepository);

    private final User user = createUser();

    private User createUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("ivan");
        u.setPassword("encoded");
        return u;
    }

    private WatchedMovie createWatchedMovie(Integer movieId, String title) {
        return new WatchedMovie(user, movieId, title, "/poster.jpg", 8, "Great movie");
    }

    @Test
    void testGetWatchedMoviesReturnsIds() {
        when(watchedMovieRepository.findMovieIdsByUser(user)).thenReturn(List.of(100, 200, 300));

        List<Integer> result = watchedMovieService.getWatchedMovies(user);

        assertEquals(3, result.size(), "Should return 3 movie IDs");
        assertEquals(100, result.get(0), "First ID should match");
    }

    @Test
    void testGetWatchedMoviesEmptyList() {
        when(watchedMovieRepository.findMovieIdsByUser(user)).thenReturn(List.of());

        List<Integer> result = watchedMovieService.getWatchedMovies(user);

        assertTrue(result.isEmpty(), "Should return empty list when no watched movies");
    }

    @Test
    void testGetWatchedMovieDetailsReturnsMappedResponses() {
        WatchedMovie movie1 = createWatchedMovie(100, "Interstellar");
        WatchedMovie movie2 = createWatchedMovie(200, "Film");
        when(watchedMovieRepository.findAllByUser(user)).thenReturn(List.of(movie1, movie2));

        List<WatchedMovieResponse> result = watchedMovieService.getWatchedMovieDetails(user);

        assertEquals(2, result.size(), "Should return 2 responses");
        assertEquals("Interstellar", result.get(0).title(), "First movie title should match");
        assertEquals("Film", result.get(1).title(), "Second movie title should match");
    }

    @Test
    void testGetRecentWatchedDetailsReturnsTop5() {
        WatchedMovie movie = createWatchedMovie(100, "Interstellar");
        when(watchedMovieRepository.findTop5ByUserOrderByWatchedAtDesc(user)).thenReturn(List.of(movie));

        List<WatchedMovieResponse> result = watchedMovieService.getRecentWatchedDetails(user);

        assertEquals(1, result.size(), "Should return 1 recent movie");
        assertEquals(100, result.get(0).movieId(), "Movie ID should match");
    }

    @Test
    void testAddWatchedMovieSavesWhenNotExists() {
        when(watchedMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.empty());
        WatchedMovieRequest request = new WatchedMovieRequest("Interstellar", "/poster.jpg", 9, "Amazing");

        watchedMovieService.addWatchedMovie(user, 100, request);

        verify(watchedMovieRepository).save(argThat(movie ->
            movie.getMovieId().equals(100) &&
            "Interstellar".equals(movie.getTitle()) &&
            movie.getRating().equals(9)
        ));
    }

    @Test
    void testAddWatchedMovieSkipsWhenAlreadyExists() {
        WatchedMovie existing = createWatchedMovie(100, "Interstellar");
        when(watchedMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.of(existing));
        WatchedMovieRequest request = new WatchedMovieRequest("Interstellar", "/poster.jpg", 9, "Amazing");

        watchedMovieService.addWatchedMovie(user, 100, request);

        verify(watchedMovieRepository, never()).save(any());
    }

    @Test
    void testRemoveWatchedMovieCallsDelete() {
        watchedMovieService.removeWatchedMovie(user, 100);

        verify(watchedMovieRepository).deleteByUserAndMovieId(user, 100);
    }

    @Test
    void testAddWatchedMovieWithNullRating() {
        when(watchedMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.empty());
        WatchedMovieRequest request = new WatchedMovieRequest("Interstellar", "/poster.jpg", null, null);

        watchedMovieService.addWatchedMovie(user, 100, request);

        verify(watchedMovieRepository).save(argThat(movie ->
            movie.getRating() == null && movie.getNotes() == null
        ));
    }
}