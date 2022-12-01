package com.teamrocket.core.unit.service.impl;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.teamrocket.core.entity.AppRole;
import com.teamrocket.core.entity.GatewayRoute;
import com.teamrocket.core.entity.RoutePath;
import com.teamrocket.core.entity.RoutePathAppRole;
import com.teamrocket.core.repository.AppRoleRepository;
import com.teamrocket.core.repository.GatewayRouteRepository;
import com.teamrocket.core.repository.RoutePathAppRoleRepository;
import com.teamrocket.core.repository.RoutePathRepository;
import com.teamrocket.core.service.GatewayRouteService;
import com.teamrocket.core.service.impl.GatewayRouteServiceImpl;
import com.teamrocket.core.service.impl.RoutePathAppRoleService;
import com.teamrocket.core.service.impl.RoutePathService;
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
        RoutePathService.class,
        RoutePathAppRoleRepository.class,
        GatewayRouteServiceImpl.class,
        GatewayRouteRepository.class,
        RoutePathRepository.class,
        AppRoleRepository.class,
    }
)
class GatewayRouteServiceImplTest {

    @Autowired
    private GatewayRouteService gatewayRouteService;

    @MockBean
    private RoutePathAppRoleRepository mockRoutePathAppRoleRepository;

    @MockBean
    private GatewayRouteRepository gatewayRouteRepository;

    @MockBean
    private RoutePathRepository routePathRepository;

    @MockBean
    private AppRoleRepository appRoleRepository;

    @Test
    @DisplayName("getAllGatewayRoutes will return all the current gateway routes")
    void getAllGatewayRoutesWillReturnAllTheCurrentGatewayRoutes() throws Exception {
        long gatewayRouteId = 1L;
        GatewayRoute expectedRoute = GatewayRoute.builder()
            .id(gatewayRouteId)
            .requestPath("/customer/**")
            .forwardUri("http://localhost:8080")
            .build();
        when(gatewayRouteRepository.findAll())
            .thenReturn(Flux.just(expectedRoute));

        RoutePath routePath = RoutePath.builder()
            .id(1L)
            .gatewayRouteId(gatewayRouteId)
            .path("/customer/**")
            .method("BEARER")
            .build();
        when(routePathRepository.findRoutePathByGatewayRouteId(eq(gatewayRouteId)))
            .thenReturn(Flux.just(routePath));

        RoutePathAppRole routePathAppRole = RoutePathAppRole.builder().appRoleId(1L).routePathId(routePath.getId()).id(1L).build();
        when(mockRoutePathAppRoleRepository.findRoutePathAppRoleByRoutePathId(eq(routePath.getId())))
            .thenReturn(Flux.just(routePathAppRole));

        when(appRoleRepository.findById(eq(routePathAppRole.getId())))
            .thenReturn(Mono.just(AppRole.builder().role("CUSTOMER").id(1L).build()));

        StepVerifier.create(gatewayRouteService.getAllGatewayRoutes())
            .expectNextCount(1)
            .expectComplete()
            .verify();
    }

    @Test
    @DisplayName("getAllGatewayRoutes when an incorrect auth method is stored")
    void getAllGatewayRoutesWillFailWhenAnIncorrectAuthMethodIsStored() throws Exception {
        long gatewayRouteId = 1L;
        GatewayRoute expectedRoute = GatewayRoute.builder()
            .id(gatewayRouteId)
            .requestPath("/customer/**")
            .forwardUri("http://localhost:8080")
            .build();
        when(gatewayRouteRepository.findAll())
            .thenReturn(Flux.just(expectedRoute));

        RoutePath routePath = RoutePath.builder()
            .id(1L)
            .gatewayRouteId(gatewayRouteId)
            .path("/customer/**")
            .method("UNKNOWN_METHOD")
            .build();
        when(routePathRepository.findRoutePathByGatewayRouteId(eq(gatewayRouteId)))
            .thenReturn(Flux.just(routePath));

        StepVerifier.create(gatewayRouteService.getAllGatewayRoutes())
            .expectError()
            .verify();
    }
}