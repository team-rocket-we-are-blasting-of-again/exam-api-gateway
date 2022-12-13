package com.teamrocket.core.acceptance;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamrocket.core.dto.GatewayRouteDto;
import com.teamrocket.core.dto.RoutePathDto;
import com.teamrocket.core.enums.AuthenticationMethod;
import com.teamrocket.core.enums.Role;
import com.teamrocket.core.util.AuthTokenUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import reactor.core.publisher.Flux;

public class GatewayRoutesDef {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthTokenUtil authTokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private GatewayRouteDto gatewayRoute;

    private String routeForGetRequests;
    private ResponseSpec requestWithPost;

    @Given("a route with the path {string}")
    public void aRouteWithThePath(String path) {
        RoutePathDto routePathDto = RoutePathDto.builder()
            .path(path)
            .method(AuthenticationMethod.BEARER)
            .rolesAllowed(List.of(Role.CUSTOMER))
            .build();

        gatewayRoute = GatewayRouteDto.builder()
            .requestPath(path)
            .forwardUri("http://localhost:8080")
            .routePathDto(List.of(routePathDto))
            .build();
    }

    @When("i ensure the same route twice")
    public void iEnsureTheSameRouteTwice() {
        for (int i = 0; i < 2; i++) {
            webTestClient.post()
                .uri("/gateway/ensure-route")
                .header(AUTHORIZATION, authTokenUtil.authToken())
                .body(Flux.just(gatewayRoute), GatewayRouteDto.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();
        }
    }

    @Then("the route is not duplicated")
    public void theRouteIsNotDuplicated() throws JsonProcessingException {
        byte[] gatewayRouteBytes = webTestClient.get()
            .uri("/gateway/route")
            .header(AUTHORIZATION, authTokenUtil.authToken())
            .exchange()
            .expectBody()
            .returnResult()
            .getResponseBody();

        TypeReference<List<GatewayRouteDto>> mapType = new TypeReference<>() {
        };
        List<GatewayRouteDto> gatewayRouteDtos = objectMapper.readValue(new String(gatewayRouteBytes), mapType)
            .stream()
            .filter(gatewayRouteDto -> gatewayRouteDto.getRequestPath().equals(gatewayRoute.getRequestPath()))
            .toList();

        assertThat(gatewayRouteDtos.size()).isEqualTo(1);
    }

    @Given("a route which is only allowed to do GET requests")
    public void aRouteWhichIsOnlyAllowedToDoRequests() {
        routeForGetRequests = "/gateway/route";
    }

    @When("a request is done with the method POST")
    public void aRequestIsDoneWithTheMethod() {
        requestWithPost = webTestClient.post()
            .uri(routeForGetRequests)
            .exchange();
    }

    @Then("the status code should be not found")
    public void theStatusCodeShouldBe() {
        requestWithPost
            .expectStatus()
            .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
