package com.teamrocket.core.acceptance.config;

import com.teamrocket.core.util.annotaion.AcceptanceTest;
import com.teamrocket.core.util.annotaion.IntegrationTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureMockMvc
@CucumberContextConfiguration
@AcceptanceTest
public class CucumberSpringContextConfig {

}
