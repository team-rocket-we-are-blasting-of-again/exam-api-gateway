package com.teamrocket.core.repository;

import com.teamrocket.core.entity.GatewayRoute;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface GatewayRouteRepository extends R2dbcRepository<GatewayRoute, Long> {

    Mono<GatewayRoute> findGatewayRouteByRequestPath(String requestPath);

    @Modifying
    @Query("UPDATE gateway_route SET forward_uri = $2 WHERE gateway_route.id = $1")
    Mono<Integer> updateForwardUri(Long gatewayRouteId, String forwardUri);

}
