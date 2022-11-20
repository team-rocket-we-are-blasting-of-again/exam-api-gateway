package com.teamrocket.gateway.entity;

import com.teamrocket.gateway.dto.RoutePathDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("route_path")
public class RoutePath {

    @Id
    @Column("id")
    private Long id;

    @Column("path")
    private String path;

    @Column("method")
    private String method;

    @Column("gateway_route_id")
    private Long gatewayRouteId;

    public static List<RoutePath> fromList(List<RoutePathDto> routePathList) {
        return routePathList
            .stream()
            .map(RoutePath::fromDto)
            .collect(Collectors.toList());
    }

    public static RoutePath fromDto(RoutePathDto routePath) {
        return RoutePath.builder()
            .id(routePath.getId())
            .path(routePath.getPath())
            .method(routePath.getMethod().toString())
            .build();
    }

}
