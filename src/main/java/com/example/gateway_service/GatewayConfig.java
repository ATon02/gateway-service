package com.example.gateway_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.gateway_service.configurationsjwt.JwtAuthenticationFilter;

@Configuration
public class GatewayConfig {

    private static final String pathUsersServicePublic = "/api/auth/**";
    private static final String pathUsersService = "/api/users/**";
    private static final String pathTasksService = "/api/tasks/**";

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator router(RouteLocatorBuilder builder){
        return  builder.routes()
                .route("userservice", route -> route.path(pathUsersService)
                        .filters(fil->fil.filter(authenticationFilter))
                        .uri("lb://user-service"))
                .route("userservice", route -> route.path(pathUsersServicePublic)
                        .uri("lb://user-service"))
                .route("tasksservice", route -> route.path(pathTasksService)
                        .filters(fil->fil.filter(authenticationFilter))
                        .uri("lb://task-service"))
                .build();
    }

}
