package com.tms.tms.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tms.tms.model.Customer;
import com.tms.tms.model.Role;
import com.tms.tms.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

public class CustomUserDetailsServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new CustomUserDetailsService(customerRepository);
    }

    @Test
    void testLoadUserByUsername_success() {
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        customer.setPwd("$2a$10$abcdef..."); // assume BCrypt encoded
        Role role = new Role();
        role.setName("ROLE_USER");
        customer.setRoles(Set.of(role));

        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals(customer.getPwd(), userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_notFound() {
        when(customerRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown@example.com");
        });

        assertEquals("User does not exist with email: unknown@example.com", exception.getMessage());
    }
}
