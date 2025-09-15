package com.tms.tms.filters;

import com.tms.tms.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JwtTokenValidatorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token == null) {
            throw new BadCredentialsException("Token is required");
        }

        Authentication authentication = JwtTokenUtil.validateToken(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        // Skip authentication for login and Swagger
        return path.equals("/auth/login") ||
                path.startsWith("/v3/api-docs") ||   // OpenAPI docs
                path.startsWith("/swagger-ui") ||   // Swagger UI
                path.startsWith("/swagger-resources") ||
                path.equals("/swagger-ui.html");
    }
}
