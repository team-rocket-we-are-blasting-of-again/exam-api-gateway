package com.teamrocket.core.security.util;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Setter
public class RouteRequestMatcher {

    private ServerWebExchange exchange;

    public Mono<Boolean> matches(String matchAgainst, String httpMethod) {
        if (nonNull(httpMethod)) {
            String requestMethod = exchange.getRequest().getMethodValue();
            if (!requestMethod.equalsIgnoreCase(httpMethod)) {
                return Mono.just(false);
            }
        }
        return ServerWebExchangeMatchers.pathMatchers(matchAgainst)
            .matches(exchange)
            .map(MatchResult::isMatch);
    }

}
