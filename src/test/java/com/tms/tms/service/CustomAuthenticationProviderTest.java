package com.tms.tms.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CustomAuthenticationProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    private CustomAuthenticationProvider authProvider;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        authProvider = new CustomAuthenticationProvider(userDetailsService);
    }

    @Test
    void testAuthenticate_success() {
        String rawPassword = "password123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        UserDetails user = User.builder()
                .username("testuser")
                .password(encodedPassword)
                .roles("USER")
                .build();

        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(user);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("testuser", rawPassword);

        var result = authProvider.authenticate(token);

        assertNotNull(result);
        assertEquals("testuser", result.getName());
        assertTrue(result.getAuthorities().size() > 0);
    }


    @Test
    void testSupports() {
        assertTrue(authProvider.supports(UsernamePasswordAuthenticationToken.class));
        assertFalse(authProvider.supports(String.class));
    }
}

