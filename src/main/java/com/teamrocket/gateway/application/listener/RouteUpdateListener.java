package com.teamrocket.gateway.application.listener;

import com.teamrocket.gateway.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
    topics = "routeUpdate",
    groupId = "gateway"
)
@Slf4j
@RequiredArgsConstructor
public class RouteUpdateListener {

    private final RouteService routeService;

    @KafkaHandler(isDefault = true)
    void listen(String message) {
        log.info("updating all routes routes");
        routeService.refreshRoutes();
    }

}
