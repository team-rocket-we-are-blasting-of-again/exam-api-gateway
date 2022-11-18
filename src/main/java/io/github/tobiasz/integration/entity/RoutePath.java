package io.github.tobiasz.integration.entity;

import io.github.tobiasz.integration.dto.RoutePathDto;
import io.github.tobiasz.integration.enums.AppRole;
import io.github.tobiasz.integration.enums.AuthenticationMethod;
import java.util.List;
import java.util.stream.Collectors;
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
public class RoutePath {

    private String path;
    private AuthenticationMethod method;
    private List<AppRole> appRoles;

    public static List<RoutePath> fromList(List<RoutePathDto> routePathList) {
        return routePathList
            .stream()
            .map(RoutePath::fromDto)
            .collect(Collectors.toList());
    }

    public static RoutePath fromDto(RoutePathDto routePath) {
        return RoutePath.builder()
            .path(routePath.getPath())
            .method(routePath.getMethod())
            .appRoles(routePath.getAppRoles())
            .build();
    }

}
