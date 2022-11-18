package io.github.tobiasz.integration.config.security.authmethod;

import io.github.tobiasz.integration.dto.RoutePathDto;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BasicAuthMethod implements AuthMethod {

    @Override
    public boolean canHandleMethod(String method) {
        return method.strip().equalsIgnoreCase("Basic");
    }

    @Override
    public Optional<Integer> authenticate(String token, RoutePathDto gatewayRouteDto) {
        // TODO: actually do something
        return Optional.of(1);
    }

}
