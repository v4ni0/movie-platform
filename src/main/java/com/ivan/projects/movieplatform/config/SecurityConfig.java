package com.ivan.projects.movieplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * The frontend origin is injected from application.properties (cors.allowed-origin).
     * This makes it easy to change per environment (dev vs prod) without touching code.
     */
    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // CSRF (Cross-Site Request Forgery) protection is disabled because this API is
            // stateless — there are no session cookies for an attacker to hijack. REST APIs
            // consumed by a JS frontend typically disable CSRF and rely on CORS + Auth headers instead.
            .csrf(AbstractHttpConfigurer::disable)

            // Wire in our CORS configuration so the browser allows cross-origin requests
            // from the frontend. Without this, the browser will block every response.
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // X-Frame-Options: SAMEORIGIN lets the H2 console (an iframe-based UI) load
            // in the browser during development. Remove this in production.
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
            )

            // STATELESS means Spring will never create an HttpSession or set a session cookie.
            // Every request must carry credentials (Basic Auth header) on its own — nothing is
            // remembered between requests on the server side.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // /auth/register is public so new users can sign up without credentials.
                // /auth/login is intentionally NOT listed here — it requires Basic Auth so that
                // Spring Security authenticates the user before the endpoint is reached.
                // Swagger and H2 console are public for development convenience.
                .requestMatchers("/auth/register", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )

            // HTTP Basic Auth: the frontend encodes credentials as Base64(username:password)
            // and sends them in the Authorization header on every request:
            //   Authorization: Basic dXNlcjpwYXNz
            // Spring Security decodes this, loads the user, and verifies the password automatically.
            .httpBasic(Customizer.withDefaults())
            .build();
    }

    /**
     * CORS (Cross-Origin Resource Sharing) configuration.
     *
     * Browsers block responses from a different origin (domain/port) by default as a
     * security measure. This bean tells Spring to add the correct CORS headers so the
     * browser permits the frontend to read our API responses.
     *
     * allowedOrigin   — only the configured frontend URL can make cross-origin calls.
     * allowedMethods  — the HTTP verbs the frontend is allowed to use.
     * allowedHeaders  — "*" allows any header, including Authorization (needed for Basic Auth).
     * allowCredentials — must be true when the frontend sends an Authorization header;
     *                    this also means allowedOrigins cannot be "*" (a browser security rule).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigin));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        // Apply this CORS policy to every endpoint in the application.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * BCrypt is the recommended password hashing algorithm — it is intentionally slow
     * and includes a random salt, making brute-force and rainbow-table attacks impractical.
     * This bean is used by AuthenticationService when saving passwords and by Spring Security
     * when verifying them during login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}