package com.example.gatewayserviceapi.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Component
public class LoginFilter extends AbstractGatewayFilterFactory<LoginFilter.Config> {

    public LoginFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String headerValue = getHeaderValue(request.getHeaders(), HttpHeaders.AUTHORIZATION);
            String identifier = request.getId();

            if (config.isLogger) {
                log.info("========> ID : [{}] Auth : [{}]", identifier, headerValue);
            }

            if (headerValue == null) {
                return onError(exchange);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(UNAUTHORIZED);

        return response.setComplete();
    }

    public static String getHeaderValue(HttpHeaders headers, String key) {
        List<String> values = headers.get(key);

        if (values == null) {
            return null;
        }

        return values.get(0);
    }

    @Data
    static class Config {
        private boolean isLogger;
    }
}
