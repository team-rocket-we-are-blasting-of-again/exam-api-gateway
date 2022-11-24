package com.teamrocket.core.service;

import com.teamrocket.core.security.util.RouteRequestMatcher;
import com.teamrocket.core.dto.GatewayRouteDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GatewayRouteService {

    Flux<GatewayRouteDto> getAllGatewayRoutes();

    Flux<Boolean> ensureGatewayRoute(GatewayRouteDto gatewayRouteDto);

    Mono<GatewayRouteDto> getMatchingRouteFromPath(RouteRequestMatcher routeRequestMatcher);
}
