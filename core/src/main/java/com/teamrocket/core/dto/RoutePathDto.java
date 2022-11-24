package com.teamrocket.core.dto;

import com.teamrocket.core.entity.AppRole;
import com.teamrocket.core.entity.RoutePath;
import com.teamrocket.core.enums.AuthenticationMethod;
import com.teamrocket.core.enums.Role;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutePathDto {

    private Long id;
    private String path;
    private AuthenticationMethod method;
    private List<Role> rolesAllowed;

    public static RoutePathDto fromEntity(RoutePath routePath, List<AppRole> appRoles) {
        List<Role> roles = appRoles.stream()
            .map(appRole -> Role.valueOf(appRole.getRole()))
            .toList();

        return RoutePathDto.builder()
            .id(routePath.getId())
            .path(routePath.getPath())
            .rolesAllowed(roles)
            .method(AuthenticationMethod.valueOf(routePath.getMethod()))
            .build();
    }


}
