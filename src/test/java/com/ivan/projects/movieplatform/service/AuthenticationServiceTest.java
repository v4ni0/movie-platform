package com.ivan.projects.movieplatform.service;

import com.ivan.projects.movieplatform.domain.User;
import com.ivan.projects.movieplatform.dto.request.RegisterRequest;
import com.ivan.projects.movieplatform.exception.UsernameTakenException;
import com.ivan.projects.movieplatform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final AuthenticationService authenticationService = new AuthenticationService(userRepository, passwordEncoder);

    @Test
    void testRegisterSuccess() {
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        authenticationService.register(new RegisterRequest("ivan", "password123"));

        verify(userRepository).save(argThat(user ->
            "ivan".equals(user.getUsername()) && "encodedPassword".equals(user.getPassword())
        ));
    }

    @Test
    void testRegisterEncodesPassword() {
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("myPassword")).thenReturn("hashed123");

        authenticationService.register(new RegisterRequest("ivan", "myPassword"));

        verify(passwordEncoder).encode("myPassword");
        verify(userRepository).save(argThat(user -> "hashed123".equals(user.getPassword())));
    }

    @Test
    void testRegisterUsernameTakenThrows() {
        User existing = new User();
        existing.setUsername("ivan");
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(existing));

        assertThrows(UsernameTakenException.class,
            () -> authenticationService.register(new RegisterRequest("ivan", "password123")),
            "Should throw UsernameTakenException when username is already taken");
    }

    @Test
    void testRegisterUsernameTakenDoesNotSave() {
        User existing = new User();
        existing.setUsername("ivan");
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.of(existing));

        assertThrows(UsernameTakenException.class,
            () -> authenticationService.register(new RegisterRequest("ivan", "password123")));

        verify(userRepository, never()).save(any());
    }
}