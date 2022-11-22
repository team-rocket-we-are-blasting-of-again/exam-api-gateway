package com.teamrocket.gateway.security.filter;

import com.teamrocket.gateway.config.properties.SecurityProperties;
import com.teamrocket.gateway.dto.GatewayRouteDto;
import com.teamrocket.gateway.errorhandling.UnauthorizedException;
import com.teamrocket.gateway.errorhandling.UnknownRouteException;
import com.teamrocket.gateway.security.authmethod.AuthMethod;
import com.teamrocket.gateway.security.util.AuthStatus;
import com.teamrocket.gateway.security.util.FoundAuthRoute;
import com.teamrocket.gateway.security.util.RouteRequestMatcher;
import com.teamrocket.gateway.service.GatewayRouteService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
@RequiredArgsConstructor
public class SecurityFilterImpl {

    private final List<AuthMethod> authMethods;
    private final GatewayRouteService gatewayRouteService;
    private final SecurityProperties securityProperties;

    public Mono<Void> tryValidateRequest(ServerWebExchange exchange, WebFilterChain chain) {
        // Make sure we don't have some cheeky hackers using a random user id
        HttpHeaders headers = exchange.getRequest()
            .mutate()
            .headers(httpHeaders -> httpHeaders.remove(securityProperties.getUserIdHeaderName()))
            .build()
            .getHeaders();

        RouteRequestMatcher routeRequestMatcher = new RouteRequestMatcher(exchange);
        return getAuthHeader(headers)
            // If the request does not require a token it will go through but if it does the token is invalid
            .switchIfEmpty(Mono.just(new String[]{"Bearer", "invalid-token"}))
            .zipWith(gatewayRouteService.getMatchingRouteFromPath(routeRequestMatcher))
            .map(this::toAuthDto)
            .flatMap(dto -> dto.authMethod.shouldAuthenticate(routeRequestMatcher, dto.gatewayRouteDto())
                .zipWith(Mono.just(dto))
                .map(tuple -> {
                    FoundAuthRoute foundAuthRoute = tuple.getT1();
                    AuthStatus authStatus = foundAuthRoute.authStatus();
                    if (authStatus.equals(AuthStatus.AUTHORIZED)) {
                        return Optional.of(exchange);
                    }

                    if (authStatus.equals(AuthStatus.INVALID_METHOD)) {
                        return Optional.<ServerWebExchange>empty();
                    }

                    AuthDto authDto = tuple.getT2();
                    Optional<Integer> userId = authDto.authMethod.authenticate(authDto.token(), foundAuthRoute.routePathDto());

                    if (userId.isEmpty()) {
                        return Optional.<ServerWebExchange>empty();
                    }

                    ServerWebExchange exchangeWithUserId = exchange.mutate().request(builder -> builder.header(
                        securityProperties.getUserIdHeaderName(),
                        String.valueOf(userId.orElseThrow()))
                    ).build();
                    return Optional.of(exchangeWithUserId);
                })
            )
            .switchIfEmpty(Mono.error(new UnknownRouteException()))
            .flatMap(exchangeOptional -> exchangeOptional
                .map(chain::filter)
                .orElse(Mono.error(new UnauthorizedException()))
            );
    }

    private static Mono<String[]> getAuthHeader(HttpHeaders headers) {
        return Mono.justOrEmpty(headers.get("Authorization"))
            .map(strings -> strings.get(0))
            .map(authHeader -> authHeader.split(" "))
            // An authorization header should always have a method such as Bearer or Basic
            .filter(strings -> strings.length > 1);
    }

    private AuthDto toAuthDto(Tuple2<String[], GatewayRouteDto> tuple2) {
        String[] authHeader = tuple2.getT1();
        return authMethods
            .stream()
            .filter(authMethod -> authMethod.canHandleMethod(authHeader[0]))
            .findFirst()
            .map(authMethod -> new AuthDto(authHeader[1], authMethod, tuple2.getT2()))
            .orElse(null);
    }

    record AuthDto(String token, AuthMethod authMethod, GatewayRouteDto gatewayRouteDto) {

    }

}
