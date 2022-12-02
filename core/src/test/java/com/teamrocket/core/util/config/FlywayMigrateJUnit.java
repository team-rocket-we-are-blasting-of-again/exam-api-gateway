package com.teamrocket.core.util.config;

import java.util.function.Consumer;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class FlywayMigrateJUnit implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        executeWithFlyway(extensionContext, Flyway::migrate);
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        executeWithFlyway(extensionContext, Flyway::clean);
    }

    private void executeWithFlyway(ExtensionContext extensionContext, Consumer<Flyway> flywayConsumer) {
        synchronized (FlywayMigrateJUnit.class) {
            ApplicationContext springContext = SpringExtension.getApplicationContext(extensionContext);
            Flyway flyway = springContext.getBean(Flyway.class);
            flywayConsumer.accept(flyway);
        }
    }

}
