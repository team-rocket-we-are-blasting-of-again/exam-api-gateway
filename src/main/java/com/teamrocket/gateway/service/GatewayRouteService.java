package com.teamrocket.gateway.service;

import com.teamrocket.gateway.security.util.RouteRequestMatcher;
import com.teamrocket.gateway.dto.GatewayRouteDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GatewayRouteService {

    Flux<GatewayRouteDto> getAllGatewayRoutes();

    Flux<Boolean> ensureGatewayRoute(GatewayRouteDto gatewayRouteDto);

    Mono<GatewayRouteDto> getMatchingRouteFromPath(RouteRequestMatcher routeRequestMatcher);
}
