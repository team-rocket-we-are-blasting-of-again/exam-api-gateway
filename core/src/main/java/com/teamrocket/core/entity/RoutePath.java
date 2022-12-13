package com.teamrocket.core.entity;

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

    @Column("http_method")
    private String httpMethod;

    @Column("gateway_route_id")
    private Long gatewayRouteId;

}
