package com.teamrocket.core.security.authmethod;

import com.teamrocket.core.config.properties.SecurityProperties;
import com.teamrocket.core.dto.RoutePathDto;
import com.teamrocket.core.security.util.VerifiedUser;
import java.util.Base64;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthMethod implements AuthMethod {

    private final SecurityProperties securityProperties;

    @Override
    public boolean canHandleMethod(String method) {
        return method.strip().equalsIgnoreCase("Basic");
    }

    @Override
    public Optional<VerifiedUser> authenticate(String token, RoutePathDto gatewayRouteDto) {
        return decodeToken(token)
            .filter(this::hasCorrectCredentials)
            .map(credentials -> new VerifiedUser(null, null));
    }

    private boolean hasCorrectCredentials(Credentials credentials) {
        return credentials.username().equals(securityProperties.getUsername()) &&
            credentials.password().equals(securityProperties.getPassword());
    }

    private Optional<Credentials> decodeToken(String token) {
        try {
            byte[] bytes = Base64.getDecoder().decode(token);
            String decodedCredentials = new String(bytes);
            String[] splitCredentials = decodedCredentials.split(":");
            if (splitCredentials.length != 2) {
                log.info("Invalid BASIC auth token provided. Invalid format after decoding {}", decodedCredentials);
                return Optional.empty();
            }
            Credentials credentials = new Credentials(splitCredentials[0], splitCredentials[1]);
            return Optional.of(credentials);
        } catch (IllegalArgumentException e) {
            log.info("Invalid BASIC auth token provided. It was not base64");
            return Optional.empty();
        }
    }

    private record Credentials(String username, String password) {
    }

}
