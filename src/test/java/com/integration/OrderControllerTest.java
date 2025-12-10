package com.integration;

import com.dto.OrderDto;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.repository.OrderRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerTest {


    /*
    @Autowired
    private OrderRepository orderRepository;


    @LocalServerPort
    private Integer port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16"))
            .withDatabaseName("ordersDB")
            .withUsername("postgres")
            .withPassword("root");


    private static WireMockServer wireMockServer;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("photos.api.base-url", () -> "http://localhost:" + wireMockServer.port());
    }

    @BeforeAll
    static void setupGlobal() {
        RestAssured.baseURI = "http://localhost";

        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
    }

    @AfterAll
    static void tearDownGlobal() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        orderRepository.deleteAll();

        wireMockServer.resetRequests();
        wireMockServer.resetMappings();
    }

    @Test
    void createOrderTest() throws Exception {
        Long userId = 1L;
        OrderDto orderDto = new OrderDto();

        wireMockServer.stubFor(get(urlEqualTo("/users/" + userId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": " + userId + ", \"name\": \"Mocked User\"}")));

        String responseBody = given()
                .when()
                .post("/users/{userId}", userId)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().body().asString();

        Order createdOrder = objectMapper.readValue(responseBody, Order.class);
        assertNotNull(createdOrder.getId());
        assertTrue(orderRepository.findById(createdOrder.getId()).isPresent());
    }

    @Test
    void updateOrderTest() throws Exception {
        Long userId = 2L;
        Order existingOrder = new Order();
        existingOrder = orderRepository.save(existingOrder);

        OrderDto updateDto = new OrderDto();

        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(updateDto))
                .when()
                .put("/{orderId}", existingOrder.getId())
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        Order updatedOrderInDb = orderRepository.findById(existingOrder.getId()).orElseThrow();
        assertEquals("updated", updatedOrderInDb.getStatus());
    }

    @Test
    void deleteOrderTest() {
        Long userId = 3L;
        Order existingOrder = new Order();
        existingOrder = orderRepository.save(existingOrder);

        given()
                .when()
                .delete("/{orderId}/status/{status}", existingOrder.getId(), true)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Order deletedOrderInDb = orderRepository.findById(existingOrder.getId()).orElseThrow();
        assertTrue(deletedOrderInDb.getDeleted());
    }

    @Test
    void findAllOrdersByUserTest() throws Exception {
        Long targetUserId = 4L;

        wireMockServer.stubFor(get(urlEqualTo("/users/" + targetUserId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": " + targetUserId + ", \"name\": \"Target User Mocked\"}")));

        Order firstOrder = new Order();
        firstOrder.setUserId(targetUserId);
        orderRepository.save(firstOrder);

        String responseBody = given()
                .when()
                .get("/users/{userId}", targetUserId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        List<Order> orders = objectMapper.readValue(responseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

        assertNotNull(orders);
        assertEquals(2, orders.size());
        assertEquals(targetUserId, orders.get(0).getUserId());
    }


    @Test
    void findAllActiveOrdersTest() throws Exception {
        Long userId = 7L;
        Order firstOrder = new Order();
        firstOrder.setUserId(userId);
        orderRepository.save(firstOrder);

        String responseBody = given()
                .when()
                .get("/active")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().body().asString();

        List<Order> activeOrders = objectMapper.readValue(responseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, Order.class));

        assertNotNull(activeOrders);
        assertEquals(1, activeOrders.size());
    }

     */
}
