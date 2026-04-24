package com.ivan.projects.movieplatform.service.recommendation;

import com.google.gson.Gson;
import com.ivan.projects.movieplatform.dto.response.RecommendationResponse;
import com.ivan.projects.movieplatform.exception.CustomRecommendationApiException;
import com.ivan.projects.movieplatform.vo.MovieRecommendation;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomApiRecommendationStrategyTest {

    private final HttpClient httpClient = mock(HttpClient.class);
    private final Gson gson = new Gson();
    private final CustomApiRecommendationStrategy strategy =
        new CustomApiRecommendationStrategy(httpClient, gson);

    private static final String VALID_RESPONSE = """
        {"recommendations":[
            {"id":100,"title":"Inception","score":0.95},
            {"id":200,"title":"Interstellar","score":0.90}
        ]}
        """;

    @SuppressWarnings("unchecked")
    private void mockHttpResponse(int statusCode, String body) throws IOException, InterruptedException {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);
    }

    @Test
    void testSuccessfulResponseReturnsRecommendations() throws Exception {
        mockHttpResponse(200, VALID_RESPONSE);

        RecommendationResponse result = strategy.recommend("sci-fi thriller", 5);

        assertNotNull(result);
        assertEquals(2, result.recommendations().size());
        assertEquals(100, result.recommendations().get(0).id());
        assertEquals("Inception", result.recommendations().get(0).title());
        assertEquals(200, result.recommendations().get(1).id());
    }

    @Test
    void testRequestUriContainsDescriptionAndTopK() throws Exception {
        mockHttpResponse(200, VALID_RESPONSE);

        strategy.recommend("sci-fi thriller", 3);

        verify(httpClient).send(
            argThat(req -> req.uri().toString().contains("top_k=3")
                && req.uri().toString().contains("description=")),
            any()
        );
    }

    @Test
    void testNon2xxStatusThrowsCustomRecommendationApiException() throws Exception {
        mockHttpResponse(500, "Internal Server Error");

        assertThrows(CustomRecommendationApiException.class,
            () -> strategy.recommend("sci-fi", 5));
    }

    @Test
    void test4xxStatusThrowsCustomRecommendationApiException() throws Exception {
        mockHttpResponse(404, "Not Found");

        assertThrows(CustomRecommendationApiException.class,
            () -> strategy.recommend("sci-fi", 5));
    }

    @Test
    void testInvalidJsonThrowsCustomRecommendationApiException() throws Exception {
        mockHttpResponse(200, "not valid json at all {{{");

        assertThrows(CustomRecommendationApiException.class,
            () -> strategy.recommend("sci-fi", 5));
    }

    @Test
    void testIOExceptionThrowsCustomRecommendationApiException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new IOException("Connection refused"));

        assertThrows(CustomRecommendationApiException.class,
            () -> strategy.recommend("sci-fi", 5));
    }

    @Test
    void testInterruptedExceptionThrowsCustomRecommendationApiException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new InterruptedException("interrupted"));

        assertThrows(CustomRecommendationApiException.class,
            () -> strategy.recommend("sci-fi", 5));
    }

    @Test
    void testResponseWithEmptyRecommendationsListIsReturnedAsIs() throws Exception {
        mockHttpResponse(200, """
            {"recommendations":[]}
            """);

        RecommendationResponse result = strategy.recommend("sci-fi", 5);

        assertNotNull(result);
        assertEquals(0, result.recommendations().size());
    }

    @Test
    void testDescriptionIsEncodedInUri() throws Exception {
        mockHttpResponse(200, VALID_RESPONSE);

        strategy.recommend("action & adventure", 5);

        verify(httpClient).send(
            argThat(req -> !req.uri().toString().contains(" ")),
            any()
        );
    }
}
