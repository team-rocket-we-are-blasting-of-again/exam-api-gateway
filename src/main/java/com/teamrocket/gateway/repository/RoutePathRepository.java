package com.teamrocket.gateway.repository;

import com.teamrocket.gateway.entity.RoutePath;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface RoutePathRepository extends R2dbcRepository<RoutePath, Long> {

    Flux<RoutePath> findRoutePathByGatewayRouteId(Long gatewayRouteId);

}
