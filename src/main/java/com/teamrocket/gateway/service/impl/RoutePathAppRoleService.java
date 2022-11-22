package com.teamrocket.gateway.service.impl;

import static org.bouncycastle.asn1.cms.CMSObjectIdentifiers.data;

import com.teamrocket.gateway.entity.AppRole;
import com.teamrocket.gateway.entity.RoutePath;
import com.teamrocket.gateway.entity.RoutePathAppRole;
import com.teamrocket.gateway.repository.RoutePathAppRoleRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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
