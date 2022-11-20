package com.teamrocket.gateway.service.impl;

import com.teamrocket.gateway.repository.GatewayRouteRepository;
import com.teamrocket.gateway.security.util.RouteRequestMatcher;
import com.teamrocket.gateway.dto.GatewayRouteDto;
import com.teamrocket.gateway.dto.RoutePathDto;
import com.teamrocket.gateway.entity.AppRole;
import com.teamrocket.gateway.entity.GatewayRoute;
import com.teamrocket.gateway.entity.RoutePath;
import com.teamrocket.gateway.entity.RoutePathAppRole;
import com.teamrocket.gateway.repository.AppRoleRepository;
import com.teamrocket.gateway.repository.RoutePathAppRoleRepository;
import com.teamrocket.gateway.repository.RoutePathRepository;
import com.teamrocket.gateway.service.GatewayRouteService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Service
@RequiredArgsConstructor
public class GatewayRouteServiceImpl implements GatewayRouteService {

    private final GatewayRouteRepository gatewayRouteRepository;
    private final RoutePathRepository routePathRepository;
    private final AppRoleRepository appRoleRepository;
    private final RoutePathAppRoleRepository routePathAppRoleRepository;
    private List<GatewayRouteDto> gatewayRouteDtoList;

    @Override
    public Flux<GatewayRouteDto> getAllGatewayRoutes() {
        gatewayRouteDtoList = new ArrayList<>();
        return gatewayRouteRepository.findAll()
            .flatMap(gatewayRoute -> routePathRepository.findRoutePathByGatewayRouteId(gatewayRoute.getId())
                .flatMap(routePath -> routePathAppRoleRepository.findRoutePathAppRoleByRoutePathId(routePath.getId())
                    .collectList()
                    .zipWith(Mono.just(routePath))
                    .flatMapMany(objects -> {
                        Flux<AppRole> appRoleFlux = Flux.empty();
                        for (RoutePathAppRole routePathAppRole : objects.getT1()) {
                            Long appRoleId = routePathAppRole.getAppRoleId();
                            appRoleFlux = Flux.concat(appRoleFlux, appRoleRepository.findById(appRoleId));
                        }
                        return appRoleFlux;
                    })
                    .collectList()
                    .zipWith(Mono.just(routePath))
                    .flatMap(objects -> {
                        RoutePathDto routePathDto = RoutePathDto.fromEntity(objects.getT2(), objects.getT1());
                        return Mono.just(routePathDto);
                    }))
                .collectList()
                .zipWith(Mono.just(gatewayRoute))
                .map(objects -> GatewayRouteDto.createGatewayRouteDto(objects.getT2(), objects.getT1()))
            )
            .doOnNext(gatewayRouteDtoList::add);
    }

    @Override
    @Transactional
    public Flux<Boolean> ensureGatewayRoute(GatewayRouteDto gatewayRouteDto) {
        return Mono.just(gatewayRouteDto)
            .flatMapMany(this::saveRoute)
            .switchIfEmpty(Mono.just(false));
    }

    private Flux<Boolean> saveRoute(GatewayRouteDto gatewayRouteDto) {
        return gatewayRouteRepository.save(GatewayRoute.fromDto(gatewayRouteDto))
            .zipWith(Mono.just(gatewayRouteDto.getRoutePathDto()))
            .flatMapMany(objects -> {
                GatewayRoute gatewayRoute = objects.getT1();
                List<RoutePathDto> routePathDtos = objects.getT2();
                Flux<Boolean> voidFlux = Flux.empty();
                for (RoutePathDto routePathDto : routePathDtos) {
                    Flux<Boolean> result = routePathRepository.save(toRoutePath(gatewayRoute, routePathDto))
                        .zipWith(Mono.just(routePathDto))
                        .map(tuple2 -> tuple2.getT2()
                            .getRolesAllowed()
                            .stream()
                            .map(role -> appRoleRepository.findAppRoleByRole(role.toString()))
                            .map(appRoleMono -> appRoleMono.zipWith(Mono.just(tuple2.getT1())))
                            .collect(Collectors.toList()))
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(appRoleMono -> appRoleMono)
                        .map(tuple2 -> {
                            AppRole appRole = tuple2.getT1();
                            RoutePath path = tuple2.getT2();
                            return routePathAppRoleRepository.findRoutePathAppRoleByAppRoleIdAndRoutePathId(
                                    appRole.getId(),
                                    path.getId()
                                )
                                .switchIfEmpty(Mono.just(toRoutePathAppRole(appRole, path)))
                                .flatMap(routePathAppRole -> {
                                    routePathAppRole.setRoutePathId(path.getId());
                                    routePathAppRole.setAppRoleId(appRole.getId());
                                    return routePathAppRoleRepository.save(routePathAppRole);
                                });
                        })
                        .flatMap(routePathAppRoleMono -> routePathAppRoleMono)
                        .flatMap(unused -> Mono.just(true));

                    voidFlux = Flux.concat(voidFlux, result);
                }
                return voidFlux;
            });
    }

    private static RoutePathAppRole toRoutePathAppRole(AppRole appRole, RoutePath path) {
        return RoutePathAppRole.builder()
            .appRoleId(appRole.getId())
            .routePathId(path.getId())
            .build();
    }

    private static RoutePath toRoutePath(GatewayRoute gatewayRoute, RoutePathDto routePathDto) {
        return RoutePath.builder()
            .gatewayRouteId(gatewayRoute.getId())
            .path(routePathDto.getPath())
            .method(routePathDto.getMethod().toString())
            .build();
    }

    @Override
    public Mono<GatewayRouteDto> getMatchingRouteFromPath(RouteRequestMatcher routeRequestMatcher) {
        return Flux.fromIterable(gatewayRouteDtoList)
            .collectList()
            .zipWith(Mono.just(routeRequestMatcher))
            .flatMap(this::matches);
    }

    private Mono<GatewayRouteDto> matches(Tuple2<List<GatewayRouteDto>, RouteRequestMatcher> tuple2) {
        return Mono.just(tuple2)
            .flatMapMany(o -> {
                Flux<Tuple2<Boolean, GatewayRouteDto>> routeFlux = Flux.empty();
                List<GatewayRouteDto> routeDtos = o.getT1();
                for (GatewayRouteDto routeDto : routeDtos) {
                    RouteRequestMatcher matcher = o.getT2();
                    Mono<Tuple2<Boolean, GatewayRouteDto>> matchesTuple = matcher.matches(routeDto.getRequestPath())
                        .map(aBoolean -> Tuples.of(aBoolean, routeDto));
                    routeFlux = Flux.concat(routeFlux, matchesTuple);
                }
                return routeFlux;
            })
            .filter(Tuple2::getT1)
            .map(Tuple2::getT2)
            .singleOrEmpty();
    }

}
