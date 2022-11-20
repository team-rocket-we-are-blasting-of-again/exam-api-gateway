package com.teamrocket.gateway.config;

import com.teamrocket.gateway.security.filter.GatewaySecurityFilter;
import com.teamrocket.gateway.service.RouteService;
import com.teamrocket.gateway.dto.GatewayRouteDto;
import com.teamrocket.gateway.service.GatewayRouteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
public class RouteConfig implements RouteLocator {

    private final GatewayRouteService gatewayRouteService;
    private final RouteLocatorBuilder routeLocatorBuilder;
    private final RouteService routeService;
    private final GatewaySecurityFilter securityFilter;

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
            boolean isInternal = gatewayRoute.getForwardUri().equals("INTERNAL");

            builder.route(String.valueOf(gatewayRoute.getId()), predicateSpec -> {
                String requestPath = gatewayRoute.getRequestPath();
                return predicateSpec
                    .path(requestPath)
                    .filters(f -> f
                        // to use this circuitBreaker functionality the dependency 'spring-cloud-starter-circuitbreaker-reactor-resilience4j' is required
//                        .circuitBreaker(config -> config.setFallbackUri("forward:" + CATCH_ALL_FALLBACK))
                        .filter(securityFilter))
//                    .uri(gatewayRoute.getForwardUri());
                    .uri(isInternal ? "http://localhost:8082" : gatewayRoute.getForwardUri());
            });
        }

        return builder;
    }
}
