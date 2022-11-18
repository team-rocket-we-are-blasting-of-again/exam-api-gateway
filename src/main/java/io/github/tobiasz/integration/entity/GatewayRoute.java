package io.github.tobiasz.integration.entity;

import io.github.tobiasz.integration.dto.GatewayRouteDto;
import io.github.tobiasz.integration.dto.RoutePathDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "gateway_route")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class GatewayRoute {

    @Id
    private String id;

    private String requestPath; // /order/get/1

    private String forwardUri; // http://order:8080/get/1

    private List<RoutePath> routePaths;

    public static GatewayRoute fromDto(GatewayRouteDto gatewayRouteDto) {
        return GatewayRoute.builder()
            .forwardUri(gatewayRouteDto.getForwardUri())
            .requestPath(gatewayRouteDto.getRequestPath())
            .routePaths(RoutePath.fromList(gatewayRouteDto.getRoutePathDto()))
            .build();
    }
}
