package com.teamrocket.gateway.controller;

import static com.teamrocket.gateway.Constants.CATCH_ALL_FALLBACK;

import com.teamrocket.gateway.dto.GatewayRouteDto;
import com.teamrocket.gateway.service.GatewayRouteService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayRouteService gatewayRouteService;

    @PostMapping("/gateway/ensure-route")
    public Flux<Boolean> createGatewayRoute(@RequestBody @Valid GatewayRouteDto gatewayRouteDto) {
        return gatewayRouteService.ensureGatewayRoute(gatewayRouteDto);
    }

    @GetMapping("/gateway/route")
    public Flux<GatewayRouteDto> getAllRoutes() {
        return gatewayRouteService.getAllGatewayRoutes();
    }

    @GetMapping(CATCH_ALL_FALLBACK)
    public ResponseEntity<FallbackDto> catchAllFallback() {
        int status = 404;
        return ResponseEntity
            .status(status)
            .body(new FallbackDto(status, "That endpoint does not exist"));
    }

    public record FallbackDto(Integer code, String message) {

    }
}
