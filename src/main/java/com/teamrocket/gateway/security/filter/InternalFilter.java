package com.teamrocket.gateway.security.filter;

import com.teamrocket.gateway.config.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InternalFilter extends WebFilterChainProxy {

    private final SecurityProperties securityProperties;
    private final SecurityFilterImpl securityFilter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().pathWithinApplication().value();
        for (String internalRoute : securityProperties.getInternalRoutes()) {
            if (path.startsWith(internalRoute)) {
                return securityFilter.tryValidateRequest(exchange, chain);
            }
        }
        return chain.filter(exchange);
    }
}
