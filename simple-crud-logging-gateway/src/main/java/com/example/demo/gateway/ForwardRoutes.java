package com.example.demo.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.route.builder.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

@Configuration
@ConditionalOnExpression("'${spring.profiles.active:}'.contains('forward')")
public class ForwardRoutes {

    @Bean
    RouteLocator forwardRouteLocator(RouteLocatorBuilder r) {
        return r.routes()
                .route("httpbin-forward", p -> p
                        .path("/bin/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("https://httpbin.org"))
                .build();
    }
}
