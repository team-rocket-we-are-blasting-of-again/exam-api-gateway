package com.teamrocket.core.repository;

import com.teamrocket.core.entity.RoutePathAppRole;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface RoutePathAppRoleRepository extends R2dbcRepository<RoutePathAppRole, Long> {

    Flux<RoutePathAppRole> findRoutePathAppRoleByRoutePathId(Long routePathId);

    Flux<RoutePathAppRole> deleteAllByRoutePathId(Long routePathId);

}
