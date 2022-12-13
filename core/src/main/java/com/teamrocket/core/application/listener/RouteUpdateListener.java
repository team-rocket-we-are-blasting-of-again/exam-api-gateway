package com.teamrocket.core.application.listener;

import com.teamrocket.core.service.RouteService;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
        // Optimally the gateway should be deployed using a stateful set, for host name consistency.
        // Otherwise, the routes would refresh a lot of times when a new pod is deployed
        groupId = "gateway-#{T(com.teamrocket.core.application.listener.RouteUpdateListener).getHostName()}"
    )
    void listen(String message) {
        log.info("updating all routes routes");
        routeService.refreshRoutes();
    }

    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown-host";
        }
    }

}
