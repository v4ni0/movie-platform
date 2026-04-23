package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.domain.UserMovie;
import com.ivan.projects.movieplatform.domain.WatchedMovie;
import com.ivan.projects.movieplatform.dto.request.UserMovieRequest;
import com.ivan.projects.movieplatform.dto.response.UserMovieResponse;
import com.ivan.projects.movieplatform.repository.UserMovieRepository;
import com.ivan.projects.movieplatform.repository.WatchedMovieRepository;
import com.ivan.projects.movieplatform.vo.MovieUserStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class UserMovieServiceTest {

    private UserMovieRepository userMovieRepository = mock(UserMovieRepository.class);
    private WatchedMovieRepository watchedMovieRepository = mock(WatchedMovieRepository.class);
    private UserMovieService userMovieService = new UserMovieService(userMovieRepository, watchedMovieRepository);

    private User user = createUser();

    private User createUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("ivan");
        u.setPassword("encoded");
        return u;
    }

    private UserMovie createUserMovie(Integer movieId, boolean favourite, boolean watchlist) {
        UserMovie um = new UserMovie(user, movieId, "Movie " + movieId, "/poster.jpg", "2024-01-01", 7.5);
        um.setFavourite(favourite);
        um.setOnWatchlist(watchlist);
        return um;
    }

    // ---- getStatus ----

    @Test
    void testGetStatusAllTrue() {
        when(watchedMovieRepository.findByUserAndMovieId(user, 100))
            .thenReturn(Optional.of(new WatchedMovie(user, 100, "Test", "/p.jpg", 8, null)));
        UserMovie um = createUserMovie(100, true, true);
        when(userMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.of(um));

        MovieUserStatus status = userMovieService.getStatus(user, 100);

        assertTrue(status.watched(), "Should be watched");
        assertTrue(status.favourite(), "Should be favourite");
        assertTrue(status.onWatchlist(), "Should be on watchlist");
    }

    @Test
    void testGetStatusAllFalse() {
        when(watchedMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.empty());
        when(userMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.empty());

        MovieUserStatus status = userMovieService.getStatus(user, 100);

        assertFalse(status.watched(), "Should not be watched");
        assertFalse(status.favourite(), "Should not be favourite");
        assertFalse(status.onWatchlist(), "Should not be on watchlist");
    }

    @Test
    void testGetStatusOnlyWatched() {
        when(watchedMovieRepository.findByUserAndMovieId(user, 100))
            .thenReturn(Optional.of(new WatchedMovie(user, 100, "Test", "/p.jpg", 8, null)));
        when(userMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.empty());

        MovieUserStatus status = userMovieService.getStatus(user, 100);

        assertTrue(status.watched(), "Should be watched");
        assertFalse(status.favourite(), "Should not be favourite");
        assertFalse(status.onWatchlist(), "Should not be on watchlist");
    }

    // ---- Favourites ----

    @Test
    void testGetFavouriteIdsReturnsList() {
        when(userMovieRepository.findFavouriteMovieIdsByUser(user)).thenReturn(List.of(10, 20));

        List<Integer> ids = userMovieService.getFavouriteIds(user);

        assertEquals(2, ids.size(), "Should return 2 favourite IDs");
    }

    @Test
    void testGetFavouriteDetailsReturnsMappedResponses() {
        UserMovie um = createUserMovie(100, true, false);
        when(userMovieRepository.findAllByUserAndFavouriteTrue(user)).thenReturn(List.of(um));

        List<UserMovieResponse> result = userMovieService.getFavouriteDetails(user);

        assertEquals(1, result.size(), "Should return 1 favourite");
        assertEquals(100, result.get(0).movieId(), "Movie ID should match");
        assertTrue(result.get(0).favourite(), "Should be marked as favourite");
    }

    @Test
    void testAddFavouriteCreatesNewWhenNotExists() {
        when(userMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.empty());
        UserMovieRequest request = new UserMovieRequest("Inception", "/poster.jpg", "2010-07-16", 8.8);

        userMovieService.addFavourite(user, 100, request);

        verify(userMovieRepository).save(argThat(um ->
            um.getMovieId().equals(100) && um.isFavourite()
        ));
    }

    @Test
    void testAddFavouriteUpdatesExistingRecord() {
        UserMovie existing = createUserMovie(100, false, true);
        when(userMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.of(existing));
        UserMovieRequest request = new UserMovieRequest("Inception", "/poster.jpg", "2010-07-16", 8.8);

        userMovieService.addFavourite(user, 100, request);

        verify(userMovieRepository).save(argThat(um ->
            um.isFavourite() && um.isOnWatchlist()
        ));
    }

    @Test
    void testRemoveFavouriteSetsFlag() {
        UserMovie existing = createUserMovie(100, true, false);
        when(userMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.of(existing));

        userMovieService.removeFavourite(user, 100);

        verify(userMovieRepository).save(argThat(um -> !um.isFavourite()));
    }

    @Test
    void testRemoveFavouriteDoesNothingWhenNotExists() {
        when(userMovieRepository.findByUserAndMovieId(user, 100)).thenReturn(Optional.empty());

        userMovieService.removeFavourite(user, 100);

        verify(userMovieRepository, never()).save(any());
    }

    // ---- Watchlist ----

    @Test
    void testGetWatchlistIdsReturnsList() {
        when(userMovieRepository.findWatchlistMovieIdsByUser(user)).thenReturn(List.of(30, 40));

        List<Integer> ids = userMovieService.getWatchlistIds(user);

        assertEquals(2, ids.size(), "Should return 2 watchlist IDs");
    }

    @Test
    void testGetWatchlistDetailsReturnsMappedResponses() {
        UserMovie um = createUserMovie(200, false, true);
        when(userMovieRepository.findAllByUserAndOnWatchlistTrue(user)).thenReturn(List.of(um));

        List<UserMovieResponse> result = userMovieService.getWatchlistDetails(user);

        assertEquals(1, result.size(), "Should return 1 watchlist item");
        assertTrue(result.get(0).onWatchlist(), "Should be on watchlist");
    }

    @Test
    void testAddToWatchlistCreatesNewWhenNotExists() {
        when(userMovieRepository.findByUserAndMovieId(user, 200)).thenReturn(Optional.empty());
        UserMovieRequest request = new UserMovieRequest("Dune", "/dune.jpg", "2021-10-22", 8.0);

        userMovieService.addToWatchlist(user, 200, request);

        verify(userMovieRepository).save(argThat(um ->
            um.getMovieId().equals(200) && um.isOnWatchlist()
        ));
    }

    @Test
    void testAddToWatchlistUpdatesExistingRecord() {
        UserMovie existing = createUserMovie(200, true, false);
        when(userMovieRepository.findByUserAndMovieId(user, 200)).thenReturn(Optional.of(existing));
        UserMovieRequest request = new UserMovieRequest("Dune", "/dune.jpg", "2021-10-22", 8.0);

        userMovieService.addToWatchlist(user, 200, request);

        verify(userMovieRepository).save(argThat(um ->
            um.isOnWatchlist() && um.isFavourite()
        ));
    }

    @Test
    void testRemoveFromWatchlistSetsFlag() {
        UserMovie existing = createUserMovie(200, false, true);
        when(userMovieRepository.findByUserAndMovieId(user, 200)).thenReturn(Optional.of(existing));

        userMovieService.removeFromWatchlist(user, 200);

        verify(userMovieRepository).save(argThat(um -> !um.isOnWatchlist()));
    }

    @Test
    void testRemoveFromWatchlistDoesNothingWhenNotExists() {
        when(userMovieRepository.findByUserAndMovieId(user, 200)).thenReturn(Optional.empty());

        userMovieService.removeFromWatchlist(user, 200);

        verify(userMovieRepository, never()).save(any());
    }
}