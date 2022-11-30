package com.teamrocket.core.security.filter;

import static java.util.Objects.nonNull;

import com.teamrocket.core.dto.GatewayRouteDto;
import com.teamrocket.core.errorhandling.UnauthorizedException;
import com.teamrocket.core.errorhandling.UnknownRouteException;
import com.teamrocket.core.security.authmethod.AuthMethod;
import com.teamrocket.core.security.util.AuthStatus;
import com.teamrocket.core.security.util.FoundAuthRoute;
import com.teamrocket.core.security.util.RouteRequestMatcher;
import com.teamrocket.core.security.util.VerifiedUser;
import com.teamrocket.core.service.GatewayRouteService;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest.Builder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityFilterImpl {

    private static final String USER_HEADER = "user_id";
    private static final String ROLE_HEADER = "role";

    private final List<AuthMethod> authMethods;
    private final GatewayRouteService gatewayRouteService;

    public Mono<Void> tryValidateRequest(ServerWebExchange exchange, WebFilterChain chain) {
        // Make sure we don't have some cheeky hackers using a random user id
        HttpHeaders headers = exchange.getRequest()
            .mutate()
            .headers(httpHeaders -> httpHeaders.remove(USER_HEADER).remove(ROLE_HEADER))
            .build()
            .getHeaders();

        RouteRequestMatcher routeRequestMatcher = new RouteRequestMatcher(exchange);
        return getAuthHeader(headers)
            // If the request does not require a token it will go through but if it does the token is invalid
            .switchIfEmpty(Mono.just(new String[]{"Basic", "invalid-token"}))
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

                    String path = foundAuthRoute.routePathDto().getPath();
                    if (authStatus.equals(AuthStatus.INVALID_METHOD)) {
                        log.info("Invalid auth method used: '{}' on path '{}'", path, foundAuthRoute.routePathDto().getMethod());
                        return Optional.<ServerWebExchange>empty();
                    }

                    AuthDto authDto = tuple.getT2();
                    String token = authDto.token();
                    Optional<VerifiedUser> user = authDto.authMethod.authenticate(token, foundAuthRoute.routePathDto());

                    if (user.isEmpty()) {
                        log.info("Invalid auth token provided: '{}' for path '{}'", token, path);
                        return Optional.<ServerWebExchange>empty();
                    }

                    VerifiedUser verifiedUser = user.get();
                    log.info(
                        "Successful login for user: '{}' with role: '{}' to path: '{}', with token: '{}'",
                        verifiedUser.userId(),
                        verifiedUser.role(),
                        path,
                        token
                    );
                    ServerWebExchange exchangeWithUserId = exchange.mutate().request(withUserHeaders(verifiedUser)).build();
                    return Optional.of(exchangeWithUserId);
                })
            )
            .switchIfEmpty(Mono.error(new UnknownRouteException()))
            .flatMap(exchangeOptional -> exchangeOptional
                .map(chain::filter)
                .orElse(Mono.error(new UnauthorizedException()))
            );
    }

    private static Consumer<Builder> withUserHeaders(VerifiedUser user) {
        return builder -> {
            if (nonNull(user.role())) {
                builder.header(ROLE_HEADER, user.role());
            }
            if (nonNull(user.userId())) {
                builder.header(USER_HEADER, String.valueOf(user.userId()));
            }
        };
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
