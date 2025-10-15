package com.example.demo.gateway;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.*;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.route.builder.*;
import org.springframework.web.reactive.function.client.*;
import org.springframework.http.MediaType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
@ConditionalOnExpression("'${spring.profiles.active:}'.contains('aggregate')")
public class AggregateConfig {

    @Bean
    RouteLocator aggregateRouteLocator(RouteLocatorBuilder r) {
        return r.routes()
                // this route only matches /aggregate/**; the filter will craft the response
                .route("aggregate-endpoint", p -> p
                        .path("/aggregate/**")
                        .uri("no://op")) // no outgoing call; we short-circuit in the filter
                .build();
    }

    @Bean
    WebClient httpbinClient(WebClient.Builder builder) {
        return builder.baseUrl("https://httpbin.org").build();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    GlobalFilter aggregateFilter(WebClient httpbin, ObjectMapper om) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            if (!path.startsWith("/aggregate")) {
                return chain.filter(exchange);
            }

            Mono<String> getMono = httpbin.get().uri("/get").retrieve().bodyToMono(String.class);
            Mono<String> headersMono = httpbin.get().uri("/headers").retrieve().bodyToMono(String.class);

            return Mono.zip(getMono, headersMono)
                    .flatMap(tuple -> {
                        try {
                            ObjectNode root = om.createObjectNode();
                            root.put("source", "gateway-aggregate");
                            root.set("httpbin_get", om.readTree(tuple.getT1()));
                            root.set("httpbin_headers", om.readTree(tuple.getT2()));

                            byte[] bytes = om.writeValueAsBytes(root);
                            ServerHttpResponse resp = exchange.getResponse();
                            resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            DataBuffer buf = resp.bufferFactory().wrap(bytes);
                            return resp.writeWith(Mono.just(buf));
                        } catch (Exception e) {
                            exchange.getResponse()
                                    .setStatusCode(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
                            byte[] bytes = ("{\"error\":\"aggregation_failed\",\"message\":\"" + e.getMessage() + "\"}")
                                    .getBytes(StandardCharsets.UTF_8);
                            DataBuffer buf = exchange.getResponse().bufferFactory().wrap(bytes);
                            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            return exchange.getResponse().writeWith(Mono.just(buf));
                        }
                    });
        };
    }
}
