package com.example.gateway_service.configurationsjwt;

import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;


import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private final SecretKey secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    public JwtUtils(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String extracUsername(String token){
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token){
        return (!isTokenExpired(token));
    }

    public Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token){
        return parseClaims(token).getExpiration().before(new Date());
    }

}
