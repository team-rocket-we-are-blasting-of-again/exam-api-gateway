package com.teamrocket.gateway.entity;

import com.teamrocket.gateway.dto.GatewayRouteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table("gateway_route")
public class GatewayRoute {

    @Id
    @Column("id")
    private Long id;

    @Column("request_path")
    private String requestPath;

    @Column("forward_uri")
    private String forwardUri;

    public static GatewayRoute fromDto(GatewayRouteDto gatewayRouteDto) {
        return GatewayRoute.builder()
            .forwardUri(gatewayRouteDto.getForwardUri())
            .requestPath(gatewayRouteDto.getRequestPath())
            .build();
    }
}
