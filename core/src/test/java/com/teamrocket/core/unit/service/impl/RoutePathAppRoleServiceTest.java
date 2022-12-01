package com.teamrocket.core.unit.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.teamrocket.core.entity.AppRole;
import com.teamrocket.core.entity.RoutePath;
import com.teamrocket.core.entity.RoutePathAppRole;
import com.teamrocket.core.repository.RoutePathAppRoleRepository;
import com.teamrocket.core.service.impl.RoutePathAppRoleService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ActiveProfiles("unit")
@SpringBootTest(
    webEnvironment = WebEnvironment.NONE,
    classes = {
        RoutePathAppRoleService.class,
        RoutePathAppRoleRepository.class
    }
)
class RoutePathAppRoleServiceTest {

    @Autowired
    private RoutePathAppRoleService routePathAppRoleService;

    @MockBean
    private RoutePathAppRoleRepository mockRoutePathAppRoleRepository;

    @Test
    void willCreateRoutePathAppRolesIfNoneExists() throws Exception {
        long routePathId = 1L;
        RoutePathAppRole expected = RoutePathAppRole.builder().appRoleId(1L).routePathId(routePathId).id(1L).build();
        when(mockRoutePathAppRoleRepository.deleteAllByRoutePathId(eq(routePathId)))
            .thenReturn(Flux.empty());
        when(mockRoutePathAppRoleRepository.save(any()))
            .thenReturn(Mono.just(expected));

        RoutePath routePath = RoutePath.builder()
            .id(routePathId)
            .build();
        List<AppRole> appRoles = List.of(
            AppRole.builder().role("CUSTOMER").build()
        );
        StepVerifier.create(routePathAppRoleService.updateOrCreateRoutePathAppRole(routePath, appRoles))
            .expectNext(expected)
            .expectComplete()
            .verify();
    }

    @Test
    @DisplayName("will update route path app roles if they already exist")
    void willUpdateRoutePathAppRolesIfTheyAlreadyExist() throws Exception {
        long routePathId = 1L;
        RoutePathAppRole expected = RoutePathAppRole.builder().appRoleId(1L).routePathId(routePathId).id(1L).build();
        when(mockRoutePathAppRoleRepository.deleteAllByRoutePathId(eq(routePathId)))
            .thenReturn(Flux.just(RoutePathAppRole.builder().routePathId(routePathId).appRoleId(2L).build()));
        when(mockRoutePathAppRoleRepository.save(any()))
            .thenReturn(Mono.just(expected));

        RoutePath routePath = RoutePath.builder()
            .id(routePathId)
            .build();
        List<AppRole> appRoles = List.of(
            AppRole.builder().role("CUSTOMER").build()
        );
        StepVerifier.create(routePathAppRoleService.updateOrCreateRoutePathAppRole(routePath, appRoles))
            .expectNext(expected)
            .expectComplete()
            .verify();
    }

}