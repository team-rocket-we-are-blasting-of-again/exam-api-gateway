package io.github.tobiasz.integration.config.security.authmethod;

import static io.github.tobiasz.integration.enums.AuthenticationMethod.NONE;

import io.github.tobiasz.integration.config.security.util.AuthStatus;
import io.github.tobiasz.integration.config.security.util.FoundAuthRoute;
import io.github.tobiasz.integration.config.security.util.RouteRequestMatcher;
import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.dto.RoutePathDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthMethod {

    boolean canHandleMethod(String method);

    Optional<Integer> authenticate(String token, RoutePathDto gatewayRouteDto);

    default Mono<FoundAuthRoute> shouldAuthenticate(RouteRequestMatcher routeRequestMatcher, GatewayRouteDto gatewayRouteDto) {
        List<Mono<FoundAuthRoute>> results = new ArrayList<>();

        List<RoutePathDto> routePathDtoList = gatewayRouteDto.getRoutePathDto();

        for (RoutePathDto routePath : routePathDtoList) {
            Mono<FoundAuthRoute> foundAuthRouteMono = routeRequestMatcher
                .matches(routePath.getPath())
                .zipWith(Mono.just(routePath))
                .map(objects -> {
                    boolean matched = objects.getT1();
                    RoutePathDto routePathDto = objects.getT2();
                    if (!matched) {
                        return new FoundAuthRoute(AuthStatus.NO_MATCH, routePathDto);
                    }

                    if (routePathDto.getMethod().equals(NONE)) {
                        return new FoundAuthRoute(AuthStatus.AUTHORIZED, routePathDto);
                    }

                    if (!canHandleMethod(routePathDto.getMethod().toString())) {
                        return new FoundAuthRoute(AuthStatus.INVALID_METHOD, routePathDto);
                    }

                    return new FoundAuthRoute(AuthStatus.NEEDS_AUTHENTICATION, routePathDto);
                })
                .filter(authStatus -> !authStatus.authStatus().equals(AuthStatus.NO_MATCH));

            results.add(foundAuthRouteMono);
        }

        return Flux.fromIterable(results)
            .flatMap(booleanMono -> booleanMono)
            .next();
    }

}
