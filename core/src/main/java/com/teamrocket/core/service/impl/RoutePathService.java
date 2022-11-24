package com.teamrocket.core.service.impl;

import com.teamrocket.core.dto.RoutePathDto;
import com.teamrocket.core.entity.GatewayRoute;
import com.teamrocket.core.entity.RoutePath;
import com.teamrocket.core.repository.RoutePathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoutePathService {

    private final RoutePathRepository routePathRepository;

    public Mono<RoutePath> createRoutePath(GatewayRoute gatewayRoute, RoutePathDto routePathDto) {
        return routePathRepository.save(toRoutePath(gatewayRoute, routePathDto));
    }

    private RoutePath toRoutePath(GatewayRoute gatewayRoute, RoutePathDto routePathDto) {
        return RoutePath.builder()
            .gatewayRouteId(gatewayRoute.getId())
            .path(routePathDto.getPath())
            .method(routePathDto.getMethod().toString())
            .build();
    }
}
