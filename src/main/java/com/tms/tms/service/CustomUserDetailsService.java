package com.tms.tms.service;

import com.tms.tms.model.Customer;
import com.tms.tms.repository.CustomerRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer =  customerRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User does not exist with email: " + username));
        List<GrantedAuthority> authorities = customer.getRoles().stream()
                .map(a -> new SimpleGrantedAuthority(a.getName()))
                .collect(Collectors.toList());

        return User.builder()
                .username(customer.getEmail())
                .password(customer.getPwd())
                .authorities(authorities)
                .build();
    }
}
