package com.example.gateway_service.configurationsjwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import jakarta.ws.rs.core.HttpHeaders;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${header.internal}")
    private String internal;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter (ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String internalHeader = exchange.getRequest().getHeaders().getFirst("internal"); // Obtener el encabezado 'internal'

        if (internalHeader != null && internalHeader.equals(internal)) {
            return chain.filter(exchange);
        }

        if(!exchange.getRequest().getPath().toString().startsWith("/api/auth")){
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    if (jwtUtils.validateToken(token)) {
                        Claims claims = jwtUtils.parseClaims(token);
                        exchange.getRequest(). mutate().header( "username", claims.getSubject()).build();
                    } else {
                        return onError(exchange,"Invalid JWT Token", HttpStatus. UNAUTHORIZED);
                    }
                } catch (Exception e) {
                    return onError(exchange, "JWT Token validation failed", HttpStatus. UNAUTHORIZED);
                }
            }else{
                return onError(exchange,"Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
            }
        }
        return chain.filter(exchange);
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus httpStatus){
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}
