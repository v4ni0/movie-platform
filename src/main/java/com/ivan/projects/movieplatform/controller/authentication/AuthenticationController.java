package com.ivan.projects.movieplatform.controller.authentication;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.dto.request.RegisterRequest;
import com.ivan.projects.movieplatform.service.authentication.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authService) {
        this.authenticationService = authService;
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) {
        authenticationService.register(request);
    }

    /**
     * Login endpoint for HTTP Basic Auth.
     *
     * How it works:
     * 1. The frontend sends a GET request with the Authorization header:
     *      Authorization: Basic Base64(username:password)
     * 2. Spring Security intercepts the request BEFORE it reaches this method,
     *    decodes the header, loads the user from the database, and verifies the password.
     * 3. If credentials are wrong, Spring Security returns 401 Unauthorized automatically
     *    and this method is never called.
     * 4. If credentials are correct, Spring injects the authenticated User via
     *    @AuthenticationPrincipal and we return their public info to the frontend.
     *
     * The frontend should store the Base64 credentials in memory and attach them to
     * every subsequent API request as the Authorization header. Since the server is
     * stateless, there is no session — credentials are re-verified on each request.
     *
     * This endpoint is protected (not in the permitAll list in SecurityConfig), so
     * it doubles as a credential-verification call: a 200 means valid, 401 means invalid.
     */
    @Operation(summary = "Verify credentials and return the authenticated user's info")
    @GetMapping("/login")
    public Map<String, Object> login(@AuthenticationPrincipal User user) {
        return Map.of(
            "id", user.getId(),
            "username", user.getUsername()
        );
    }
}