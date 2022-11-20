package com.teamrocket.gateway.repository;

import com.teamrocket.gateway.entity.GatewayRoute;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayRouteRepository extends R2dbcRepository<GatewayRoute, Long> {

}
