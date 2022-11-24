package com.teamrocket.core.service.impl;

import com.teamrocket.core.entity.AppRole;
import com.teamrocket.core.entity.RoutePath;
import com.teamrocket.core.entity.RoutePathAppRole;
import com.teamrocket.core.repository.RoutePathAppRoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoutePathAppRoleService {

    private final RoutePathAppRoleRepository routePathAppRoleRepository;

    public Flux<RoutePathAppRole> updateOrCreateRoutePathAppRole(RoutePath routePath, List<AppRole> appRoles) {
        return routePathAppRoleRepository.deleteAllByRoutePathId(routePath.getId())
            .collectList()
            .zipWith(Mono.just(appRoles))
            .flatMapMany(objects -> Flux.fromIterable(objects.getT2()))
            .zipWith(Flux.just(routePath))
            .flatMap(tuple2 -> routePathAppRoleRepository.save(toRoutePathAppRole(tuple2.getT1(), tuple2.getT2())));
    }

    private RoutePathAppRole toRoutePathAppRole(AppRole appRole, RoutePath path) {
        return RoutePathAppRole.builder()
            .appRoleId(appRole.getId())
            .routePathId(path.getId())
            .build();
    }
}
