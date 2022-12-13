package com.teamrocket.core.util.annotaion;

import com.teamrocket.core.util.config.FlywayMigrateJUnit;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("integration")
@ExtendWith(FlywayMigrateJUnit.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "server.port=0"
)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegrationTest {

}
