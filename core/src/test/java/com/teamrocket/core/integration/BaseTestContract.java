package com.teamrocket.core.integration;

import com.teamrocket.core.util.AuthTokenUtil;
import com.teamrocket.core.util.annotaion.IntegrationTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

@IntegrationTest
public class BaseTestContract {

    @Autowired
    private AuthTokenUtil authTokenUtil;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:%d".formatted(port);
    }

    public String authToken() {
        return authTokenUtil.authToken();
    }

    public String invalidToken() {
        return authTokenUtil.invalidToken();
    }

}
