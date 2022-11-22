package com.teamrocket.gateway.security.util;

import com.teamrocket.gateway.dto.RoutePathDto;

public record FoundAuthRoute(AuthStatus authStatus, RoutePathDto routePathDto) {

}
