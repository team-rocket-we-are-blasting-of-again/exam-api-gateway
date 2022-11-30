package com.teamrocket.core.integration;

import com.teamrocket.core.config.properties.SecurityProperties;
import com.teamrocket.core.util.annotaion.IntegrationTest;
import io.restassured.RestAssured;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

@IntegrationTest
public class BaseTestContract {

    @Autowired
    private SecurityProperties securityProperties;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:%d".formatted(port);
    }

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
