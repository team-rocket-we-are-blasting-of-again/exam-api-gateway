package io.github.tobiasz.integration.config.security.util;

import io.github.tobiasz.integration.dto.RoutePathDto;

public record FoundAuthRoute(AuthStatus authStatus, RoutePathDto routePathDto) {

}
