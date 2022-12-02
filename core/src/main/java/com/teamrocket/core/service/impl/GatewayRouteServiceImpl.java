package com.teamrocket.core.service.impl;

import com.teamrocket.core.dto.GatewayRouteDto;
import com.teamrocket.core.dto.RoutePathDto;
import com.teamrocket.core.entity.AppRole;
import com.teamrocket.core.entity.GatewayRoute;
import com.teamrocket.core.entity.RoutePath;
import com.teamrocket.core.entity.RoutePathAppRole;
import com.teamrocket.core.repository.AppRoleRepository;
import com.teamrocket.core.repository.GatewayRouteRepository;
import com.teamrocket.core.repository.RoutePathAppRoleRepository;
import com.teamrocket.core.repository.RoutePathRepository;
import com.teamrocket.core.security.util.RouteRequestMatcher;
import com.teamrocket.core.service.GatewayRouteService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private final RoutePathService routePathService;
    private final RoutePathAppRoleService routePathAppRoleService;

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
            .flatMapMany(this::editIfNeeded)
            .switchIfEmpty(Mono.just(false));
    }

    private Flux<Boolean> editIfNeeded(GatewayRouteDto gatewayRouteDto) {
        return updateOrCreateGatewayRoute(gatewayRouteDto)
            .flatMap(gatewayRoute -> routePathRepository.deleteAllByGatewayRouteId(gatewayRoute.getId())
                .collectList()
                .flatMap(unused -> Mono.just(gatewayRoute)))
            .zipWith(Mono.just(gatewayRouteDto.getRoutePathDto()))
            .flatMapMany(this::createGatewayRoute);
    }

    private Flux<Boolean> createGatewayRoute(Tuple2<GatewayRoute, List<RoutePathDto>> objects) {
        GatewayRoute gatewayRoute = objects.getT1();
        List<RoutePathDto> routePathDtos = objects.getT2();
        Flux<Boolean> voidFlux = Flux.empty();
        for (RoutePathDto routePathDto : routePathDtos) {
            Flux<Boolean> result = routePathService.createRoutePath(gatewayRoute, routePathDto)
                .zipWith(Mono.just(routePathDto))
                .map(tuple2 -> tuple2.getT2()
                    .getRolesAllowed()
                    .stream()
                    .map(role -> appRoleRepository.findAppRoleByRole(role.toString())
                        .zipWith(Mono.just(tuple2.getT1())))
                    .collect(Collectors.toList()))
                .flatMapMany(Flux::fromIterable)
                .flatMap(appRoleMono -> appRoleMono)
                .collectList()
                .flatMapMany(tuple2List -> {
                    if (tuple2List.isEmpty()) {
                        return Mono.just(true);
                    }
                    RoutePath routePath = tuple2List.get(0).getT2();
                    List<AppRole> appRoles = tuple2List
                        .stream()
                        .map(Tuple2::getT1)
                        .collect(Collectors.toList());
                    return routePathAppRoleService.updateOrCreateRoutePathAppRole(routePath, appRoles);
                })
                .map(o -> o)
                .flatMap(unused -> Mono.just(true));

            voidFlux = Flux.concat(voidFlux, result);
        }
        return voidFlux;
    }

    private Mono<GatewayRoute> updateOrCreateGatewayRoute(GatewayRouteDto gatewayRouteDto) {
        return gatewayRouteRepository.findGatewayRouteByRequestPath(gatewayRouteDto.getRequestPath())
            .map(Optional::of)
            .defaultIfEmpty(Optional.empty())
            .flatMap(gatewayRoute -> {
                if (gatewayRoute.isEmpty()) {
                    return gatewayRouteRepository.save(GatewayRoute.fromDto(gatewayRouteDto));
                }
                return Mono.just(gatewayRoute.get());
            })
            .zipWith(Mono.just(gatewayRouteDto))
            .flatMap(objects -> {
                GatewayRoute route = objects.getT1();
                GatewayRouteDto routeDto = objects.getT2();
                return gatewayRouteRepository.updateForwardUri(route.getId(), routeDto.getForwardUri())
                    .zipWith(Mono.just(route.getId()))
                    .flatMap(tuple2 -> gatewayRouteRepository.findById(tuple2.getT2()));
            });
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
