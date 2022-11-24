package com.teamrocket.core.security.authmethod;

import static com.teamrocket.core.enums.AuthenticationMethod.NONE;

import com.teamrocket.core.security.util.RouteRequestMatcher;
import com.teamrocket.core.security.util.AuthStatus;
import com.teamrocket.core.security.util.FoundAuthRoute;
import com.teamrocket.core.dto.GatewayRouteDto;
import com.teamrocket.core.dto.RoutePathDto;
import com.teamrocket.core.security.util.VerifiedUser;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuthMethod {

    boolean canHandleMethod(String method);

    Optional<VerifiedUser> authenticate(String token, RoutePathDto gatewayRouteDto);

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
