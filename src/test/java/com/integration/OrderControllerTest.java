package com.integration;

import com.dto.OrderDto;
import com.dto.UserOrdersResponse;
import com.entity.Order;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.spring.EnableWireMock;
import java.math.BigDecimal;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.springframework.http.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock
@Testcontainers
class OrderControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("ordersDB")
            .withUsername("postgres")
            .withPassword("root");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("external.user-service.url", () -> wireMockServer.baseUrl());
    }

    @Value("${wiremock.server.baseUrl}")
    private String wireMockUrl;

    static WireMockServer wireMockServer = new WireMockServer();


    private void stubUserServiceGetUserInfo(String email) {
        wireMockServer.stubFor(get(urlPathEqualTo("/email/"))
                .withQueryParam("email", equalTo(email))
                .willReturn(okJson("""
                {
                  "id": 4,
                   "email": "alice.w@gmail.com",
                   "first_name": "Alice",
                   "surname": "Williams"
                }
                """.formatted(email))));
    }


    private HttpHeaders headersWithAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " +
                "eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoiVVNFUiIsImlkIjozLCJzdWIiOiJwb2xpbmFAZ21haWwuY29tIiwiaWF0IjoxNzY1NTYxNTA2LCJleHAiOjE3NjU1OTc1MDZ9.7bDdutVaJ0aGuuNjrcckH2pFptMhX8-po4xTyOwDUUZwK28OEC0Q4wJU4Ks44apG");
        return headers;
    }


    @Test
    void testCreateOrder() {
        // Arrange
        stubUserServiceGetUserInfo("alice.w@gmail.com");

        OrderDto dto = new OrderDto( );
        dto.setId(1L);
        dto.setTotalPrice(BigDecimal.valueOf(115.20));
        dto.setStatus("created");


        HttpEntity<OrderDto> request = new HttpEntity<>(dto, headersWithAuth());

        // Act
        ResponseEntity<UserOrdersResponse> response = restTemplate.postForEntity(
                "/app/order", request, UserOrdersResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testDeleteOrder() {
        // Arrange
        HttpHeaders headers = headersWithAuth();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        OrderDto dto = new OrderDto();


        boolean status = true;

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/app/order/{orderId}/status/{status}",
                HttpMethod.DELETE,
                request,
                Void.class,
                dto.getId(), status);

        // Assert
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testUpdateOrder() {
        // Arrange
        stubUserServiceGetUserInfo("alice.w@gmail.com");
        Long orderId = 123L;
        OrderDto dto = new OrderDto();

        HttpEntity<OrderDto> request = new HttpEntity<>(dto, headersWithAuth());

        // Act
        ResponseEntity<UserOrdersResponse> response = restTemplate.exchange(
                "/app/order/{orderId}",
                HttpMethod.PUT,
                request,
                UserOrdersResponse.class,
                orderId);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void testFindAllOrdersByMe() {
        // Arrange
        HttpHeaders headers = headersWithAuth();
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<Order[]> response = restTemplate.exchange(
                "/app/order/me",
                HttpMethod.GET,
                request,
                Order[].class);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

}