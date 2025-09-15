package com.tms.tms.controllers;

import com.tms.tms.dto.JwtRequest;
import com.tms.tms.dto.JwtResponse;
import com.tms.tms.model.Customer;
import com.tms.tms.repository.CustomerRepository;
import com.tms.tms.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Customer", description = "Customer Registration and Login")
public class CustomerController {

    final CustomerRepository customerRepository;
    final PasswordEncoder passwordEncoder;
    final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    public CustomerController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil, AuthenticationManager authenticationManager) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("auth/register")
    @Operation(summary = "Registers a new customer")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer){
        try {
            String hashedPassword = passwordEncoder.encode(customer.getPwd());
            customer.setPwd(hashedPassword);
            Customer savedCustomer = customerRepository.saveAndFlush(customer);

            if (savedCustomer.getId() > 0) {
                return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User registration failed");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @PostMapping("auth/login")
    @Operation(summary = "Returns JWT token for an existing customer")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = "";

        if(authentication.isAuthenticated()) {
            token = JwtTokenUtil.generateToken(authentication);
        }

        return ResponseEntity.ok(new JwtResponse(token));
    }
}
