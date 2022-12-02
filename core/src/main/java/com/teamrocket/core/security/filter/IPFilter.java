package com.teamrocket.core.security.filter;

import static java.util.Objects.nonNull;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class IPFilter extends WebFilterChainProxy implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return filter(exchange, (WebFilterChain) chain::filter);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getPath().pathWithinApplication().value();
        log.info("{}: '{} {}'", getIp(request), request.getMethodValue(), requestPath);
        return chain.filter(exchange);
    }

    private Object getIp(ServerHttpRequest request) {
        List<String> ipAddress = request.getHeaders().get("X-Forward-For");
        if (nonNull(ipAddress) && !ipAddress.isEmpty()) {
            return ipAddress.get(0);
        }
        return request.getRemoteAddress();
    }
}
