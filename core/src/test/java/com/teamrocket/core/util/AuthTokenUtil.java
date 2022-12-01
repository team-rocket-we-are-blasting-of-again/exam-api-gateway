package com.teamrocket.core.util;

import com.teamrocket.core.config.properties.SecurityProperties;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthTokenUtil {

    private final SecurityProperties securityProperties;

    public String authToken() {
        Encoder encoder = Base64.getEncoder();
        String auth = "%s:%s".formatted(securityProperties.getUsername(), securityProperties.getPassword());
        String encodedBasicAuthToken = encoder.encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic %s".formatted(encodedBasicAuthToken);
    }

    public String invalidToken() {
        Encoder encoder = Base64.getEncoder();
        String auth = "something:1234";
        String encodedBasicAuthToken = encoder.encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic %s".formatted(encodedBasicAuthToken);
    }

}
