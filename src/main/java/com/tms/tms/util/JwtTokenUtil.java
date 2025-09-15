package com.tms.tms.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtTokenUtil {
    public static final int EXPIRE_AFTER = 1800000; // 30 mins
    public static final String JWT_TOKEN = "JWT Token";
    public static final String USERNAME = "username";
    public static final String AUTHORITIES = "authorities";
    public static final String DEMO_APP = "Demo App";
    private static final String JWT_DEFAULT_SECRET_VALUE = "8a8e2a87-4f4e-4097-b84a-48bf70303eda"; // Will read from env variable

    public static String generateToken(Authentication authentication) {
        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_DEFAULT_SECRET_VALUE.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .issuer(DEMO_APP)
                .subject(JWT_TOKEN)
                .claim(USERNAME, authentication.getName())
                .claim(AUTHORITIES, authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + EXPIRE_AFTER)))
                .signWith(secretKey)
                .compact();
    }

    public static Authentication validateToken(String jwt) {
        SecretKey secretKey = Keys.hmacShaKeyFor(JWT_DEFAULT_SECRET_VALUE.getBytes(StandardCharsets.UTF_8));
        if(jwt != null) {
            try {
                Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();
                String username = String.valueOf(claims.get(USERNAME));
                String authorities = String.valueOf(claims.get(AUTHORITIES));
                return new UsernamePasswordAuthenticationToken(username, null,
                        Arrays.stream(authorities.split(",")).map(SimpleGrantedAuthority::new).toList());
            } catch (Exception e) {
                throw new BadCredentialsException("Invalid token");
            }
        }
        return null;
    }


}
