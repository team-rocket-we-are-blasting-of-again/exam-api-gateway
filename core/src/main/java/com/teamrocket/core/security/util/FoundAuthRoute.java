package com.teamrocket.core.security.util;

import com.teamrocket.core.dto.RoutePathDto;

public record FoundAuthRoute(AuthStatus authStatus, RoutePathDto routePathDto) {

}
