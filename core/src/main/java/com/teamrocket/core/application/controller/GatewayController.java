package com.teamrocket.core.application.controller;

import com.teamrocket.core.dto.GatewayRouteDto;
import com.teamrocket.core.service.GatewayRouteService;
import com.teamrocket.core.service.KafkaService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GatewayController {

    private final GatewayRouteService gatewayRouteService;
    private final KafkaService kafkaService;

    @PostMapping("/gateway/ensure-route")
    public Flux<Boolean> ensureRoute(@RequestBody @Valid List<GatewayRouteDto> gatewayRouteDtos) {
        log.info("ensureRoute");
        return Flux.fromIterable(gatewayRouteDtos)
            .flatMap(gatewayRouteService::ensureGatewayRoute)
            .doOnComplete(() -> kafkaService.send("routeUpdate", ""));
    }

    @GetMapping("/gateway/route")
    public Flux<GatewayRouteDto> getAllRoutes() {
        log.info("getAllRoutes");
        return gatewayRouteService.getAllGatewayRoutes();
    }
}
