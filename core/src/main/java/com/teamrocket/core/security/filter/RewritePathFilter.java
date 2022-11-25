package com.teamrocket.core.security.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class RewritePathFilter extends AbstractGatewayFilterFactory<RewritePathFilter.Config> {

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest req = exchange.getRequest();
            addOriginalRequestUrl(exchange, req.getURI());
            String path = req.getURI().getRawPath();
            String newPath = path.replaceAll(
                "%s/(?<segment>.*)".formatted(getFirstSegment(config.requestPath())),
                "/${segment}"
            );
            ServerHttpRequest request = req.mutate().path(newPath).build();
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, request.getURI());
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    private String getFirstSegment(String requestPath) {
        requestPath = removeSuffix("/**", requestPath);
        requestPath = removeSuffix("/*", requestPath);
        return removeSuffix("/", requestPath);
    }

    private String removeSuffix(String suffix, String requestPath) {
        if (requestPath.endsWith(suffix)) {
            return requestPath.substring(0, requestPath.length() - suffix.length());
        }
        return requestPath;
    }

    public record Config(String requestPath) {

    }
}
