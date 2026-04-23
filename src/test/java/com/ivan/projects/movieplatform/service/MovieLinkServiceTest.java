package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.MovieLink;
import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.dto.request.MovieLinkRequest;
import com.ivan.projects.movieplatform.dto.response.MovieLinkResponse;
import com.ivan.projects.movieplatform.repository.MovieLinkRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class MovieLinkServiceTest {

    private MovieLinkRepository movieLinkRepository = mock(MovieLinkRepository.class);
    private MovieLinkService movieLinkService = new MovieLinkService(movieLinkRepository);

    private User user = createUser();

    private User createUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("ivan");
        u.setPassword("encoded");
        return u;
    }

    @Test
    void testGetAllLinksReturnsMappedResponses() {
        MovieLink link1 = new MovieLink(user, "https://example.com/movie1", 8);
        MovieLink link2 = new MovieLink(user, "https://example.com/movie2", 6);
        when(movieLinkRepository.findAllByUser(user)).thenReturn(List.of(link1, link2));

        List<MovieLinkResponse> result = movieLinkService.getAllLinks(user);

        assertEquals(2, result.size(), "Should return 2 links");
        assertEquals("https://example.com/movie1", result.get(0).url(), "First URL should match");
        assertEquals(8, result.get(0).rating(), "First rating should match");
    }

    @Test
    void testGetAllLinksReturnsEmptyList() {
        when(movieLinkRepository.findAllByUser(user)).thenReturn(List.of());

        List<MovieLinkResponse> result = movieLinkService.getAllLinks(user);

        assertTrue(result.isEmpty(), "Should return empty list when no links");
    }

    @Test
    void testAddLinkSavesCorrectEntity() {
        MovieLinkRequest request = new MovieLinkRequest("https://example.com/trailer", 9);

        movieLinkService.addLink(user, request);

        verify(movieLinkRepository).save(argThat(link ->
            "https://example.com/trailer".equals(link.getUrl()) &&
            link.getRating().equals(9) &&
            link.getUser().equals(user) &&
            link.getAddedAt() != null
        ));
    }

    @Test
    void testRemoveLinkCallsDeleteByIdAndUser() {
        movieLinkService.removeLink(user, 42L);

        verify(movieLinkRepository).deleteByIdAndUser(42L, user);
    }
}