package com.example.demo.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.*;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.route.builder.*;

@Configuration
@ConditionalOnExpression("'${spring.profiles.active:}'.contains('rewrite')")
public class RewriteRoutes {

    @Bean
    RouteLocator rewriteRouteLocator(RouteLocatorBuilder r) {
        return r.routes()
                .route("httpbin-rewrite-anything", p -> p
                        .path("/bin/**")
                        .filters(f -> f.rewritePath("/bin/(?<seg>.*)", "/anything/${seg}"))
                        .uri("https://httpbin.org"))
                .build();
    }
}
