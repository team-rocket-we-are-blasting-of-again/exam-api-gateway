package com.teamrocket.gateway.repository;

import com.teamrocket.gateway.entity.RoutePathAppRole;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoutePathAppRoleRepository extends R2dbcRepository<RoutePathAppRole, Long> {

    Flux<RoutePathAppRole> findRoutePathAppRoleByRoutePathId(Long routePathId);

    Flux<RoutePathAppRole> deleteAllByRoutePathId(Long routePathId);

}
