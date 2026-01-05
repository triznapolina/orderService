package com.integration;

import com.dto.OrderDto;
import com.entity.CurrentUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;



@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class OrderControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("ordersDB")
            .withUsername("postgres")
            .withPassword("root");

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:2.35.0")
            .withExposedPorts(8080);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        String wiremockUrl = String.format("http://%s:%d",
                wireMockContainer.getHost(),
                wireMockContainer.getMappedPort(8080));
        registry.add("external.api.base-url", () -> wiremockUrl);
    }

    @Test
    void shouldReturnUserByEmail() throws Exception {
        String email = "user@gmail.com";
        String responseBody = """
                {
                     "id": 101,
                     "name": "Palina Tryzna"
                }
                """;

        WireMock wireMock = new WireMock(
                wireMockContainer.getHost(),
                wireMockContainer.getMappedPort(8080)
        );

        wireMock.register(WireMock.get(WireMock.urlPathEqualTo("/user"))
                .withQueryParam("email", WireMock.equalTo(email))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody))
        );

        String wmUrl = String.format("http://%s:%d/user?email=%s",
                wireMockContainer.getHost(),
                wireMockContainer.getMappedPort(8080),
                email);

        HttpURLConnection connection = (HttpURLConnection) new URL(wmUrl).openConnection();
        connection.setRequestMethod("GET");

        int status = connection.getResponseCode();
        assertEquals(200, status, "Response must be 200 OK");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            String json = result.toString();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            assertTrue(root.has("id"), "JSON contains 'id'");
            assertEquals(101, root.get("id").asInt(), "'id' equals 101");
            assertTrue(root.has("name"), "JSON contains 'name'");
            assertEquals("Palina Tryzna", root.get("name").asText(), "'name' must be 'Palina Tryzna'");

            System.out.println("Response: " + json);
        }
    }

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {
        String email = "user@gmail.com";

        String orderDtoJson = """
        {
            "status": "created",
            "totalPrice": 199.99
        }
        """;

        String userResponseBody = """
        {
            "id": 101,
            "name": "Palina Tryzna",
            "email": "user@gmail.com"
        }
        """;

        WireMock wireMock = new WireMock(
                wireMockContainer.getHost(),
                wireMockContainer.getMappedPort(8080)
        );

        wireMock.register(WireMock.get(WireMock.urlPathEqualTo("/user"))
                .withQueryParam("email", WireMock.equalTo(email))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(userResponseBody))
        );



        CurrentUser currentUser = new CurrentUser("user@gmail.com", 1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/app/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderDtoJson)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(
                                        currentUser,
                                        null,
                                        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        )))
                .andExpect(status().isCreated());


    }


    @Test
    void shouldReturnOrdersForAuthenticatedUser() throws Exception {

        Long userId = 1L;

        OrderDto order1 = new OrderDto();
        order1.setStatus("created");
        order1.setTotalPrice(BigDecimal.valueOf(100));
        orderService.createOrder(order1, userId);

        OrderDto order2 = new OrderDto();
        order2.setStatus("completed");
        order2.setTotalPrice(BigDecimal.valueOf(200));
        orderService.createOrder(order2, userId);

        CurrentUser currentUser = new CurrentUser("user@gmail.com", userId);

        mockMvc.perform(MockMvcRequestBuilders.get("/app/order/me")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(
                                        currentUser,
                                        null,
                                        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        )))
                .andExpect(status().isOk());
    }



}