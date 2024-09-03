package com.example.gateway_service;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private static final String pathUsersService = "/api/users/**";
    private static final String pathTasksService = "/api/tasks/**";

    @Bean
    public RouteLocator router(RouteLocatorBuilder builder){
        return  builder.routes()
                .route("userservice", route -> route.path(pathUsersService)
                        .uri("lb://user-service"))
                .route("petservice", route -> route.path(pathTasksService)
                        .uri("lb://task-service"))
                .build();
    }

}
