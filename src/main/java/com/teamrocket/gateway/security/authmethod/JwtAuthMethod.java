package com.teamrocket.gateway.security.authmethod;

import com.teamrocket.gateway.dto.RoutePathDto;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthMethod implements AuthMethod {

    @Override
    public boolean canHandleMethod(String method) {
        return method.strip().equalsIgnoreCase("Bearer");
    }

    @Override
    public Optional<Integer> authenticate(String token, RoutePathDto routePathDto) {
        // TODO: Call auth service
        // TODO: Check if the found role is allowed on that route
        return Optional.of(1);
    }

}
