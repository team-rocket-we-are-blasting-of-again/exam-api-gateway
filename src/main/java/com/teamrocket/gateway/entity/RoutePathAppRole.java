package com.teamrocket.gateway.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("route_path_app_role")
public class RoutePathAppRole {

    @Column("id")
    private Long id;

    @Column("route_path_id")
    private Long routePathId;

    @Column("app_role_id")
    private Long appRoleId;

}
