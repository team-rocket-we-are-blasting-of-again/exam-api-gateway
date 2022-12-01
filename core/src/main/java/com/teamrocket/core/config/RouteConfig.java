package com.teamrocket.core.config;

import com.teamrocket.core.dto.GatewayRouteDto;
import com.teamrocket.core.security.filter.GatewaySecurityFilter;
import com.teamrocket.core.security.filter.IPFilter;
import com.teamrocket.core.security.filter.RewritePathFilter;
import com.teamrocket.core.security.filter.RewritePathFilter.Config;
import com.teamrocket.core.service.GatewayRouteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class RouteConfig implements RouteLocator {

    private final GatewayRouteService gatewayRouteService;
    private final RouteLocatorBuilder routeLocatorBuilder;
    private final GatewaySecurityFilter securityFilter;
    private final RewritePathFilter rewritePathFilter;
    private final IPFilter ipFilter;

    @Override
    public Flux<Route> getRoutes() {
        return gatewayRouteService.getAllGatewayRoutes()
            .collectList()
            .zipWith(Mono.just(routeLocatorBuilder.routes()))
            .map(this::buildRoute)
            .flatMapMany(builder -> builder.build().getRoutes());
    }

    private Builder buildRoute(Tuple2<List<GatewayRouteDto>, Builder> objects) {
        Builder builder = objects.getT2();
        List<GatewayRouteDto> gatewayRouteList = objects.getT1();
        for (GatewayRouteDto gatewayRoute : gatewayRouteList) {
            builder.route(String.valueOf(gatewayRoute.getId()), predicateSpec -> {
                String requestPath = gatewayRoute.getRequestPath();
                boolean isInternal = gatewayRoute.getForwardUri().equals("INTERNAL");

                return predicateSpec
                    .path(requestPath)
                    .filters(f -> f
                        .filter(ipFilter)
                        .filter(securityFilter)
                        .filter(rewritePathFilter.apply(new Config(requestPath)))
                    )
                    .uri(isInternal ? "http://localhost:8082" : gatewayRoute.getForwardUri());
            });
        }

        return builder;
    }

}
