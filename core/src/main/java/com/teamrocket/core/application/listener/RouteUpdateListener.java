package com.teamrocket.core.application.listener;

import com.teamrocket.core.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RouteUpdateListener {

    private final RouteService routeService;

    @KafkaListener(
        topics = "routeUpdate",
        // This will make sure that all gateway instances get the message, and will refresh the routes
        groupId = "gateway-#{T(java.util.UUID).randomUUID().toString()}"
    )
    void listen(String message) {
        log.info("updating all routes routes");
        routeService.refreshRoutes();
    }

}
