package com.teamrocket.gateway.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Value("${spring.flyway.url}")
    private String flywayUrl;

    @Value("${spring.flyway.user}")
    private String flywayUser;

    @Value("${spring.flyway.password}")
    private String flywayPassword;

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        FluentConfiguration configuration = Flyway
            .configure()
            .dataSource(flywayUrl, flywayUser, flywayPassword);

        return new Flyway(configuration);
    }

}
