package com.teamrocket.core.dto;


import com.teamrocket.core.entity.GatewayRoute;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatewayRouteDto {

    private Long id;

    @NotBlank
    private String requestPath;

    @NotBlank
    private String forwardUri;

    private List<RoutePathDto> routePathDto = new ArrayList<>();

    public static GatewayRouteDto createGatewayRouteDto(GatewayRoute gatewayRoute, List<RoutePathDto> routePaths) {
        return GatewayRouteDto.builder()
            .id(gatewayRoute.getId())
            .requestPath(gatewayRoute.getRequestPath())
            .forwardUri(gatewayRoute.getForwardUri())
            .routePathDto(routePaths)
            .build();
    }
}
