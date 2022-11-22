package com.teamrocket.gateway.config.properties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("io.github.tobiasz.security")
@Getter
@Setter
public class SecurityProperties {

    @NotBlank
    private String userIdHeaderName;

    @NotNull
    private GatewayAuthMethod gatewayAuthMethod;

    private String[] internalRoutes;

    enum GatewayAuthMethod {
        BASIC, JWT
    }

}
