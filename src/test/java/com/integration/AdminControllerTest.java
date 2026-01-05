package com.integration;

import com.dto.ItemDto;
import com.entity.CurrentUser;
import com.entity.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repository.ItemRepository;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;



@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AdminControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("ordersDB")
            .withUsername("postgres")
            .withPassword("root");

    @Autowired
    private MockMvc mockMvc;


    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

    }

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createItem() throws Exception {

        Long userId = 1L;
        ItemDto itemDto = new ItemDto("Gaming Laptop", new BigDecimal("150000.00"));
        String requestJson = objectMapper.writeValueAsString(itemDto);

        CurrentUser currentUser = new CurrentUser("user@gmail.com", userId);

        mockMvc.perform(post("/app/admin-order/add-item")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(
                                        currentUser,
                                        null,
                                        Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                )
                        )))
                .andExpect(status().isCreated());
    }

    @Test
    void updateItem() throws Exception {

        Long userId = 1L;
        Item existingItem = new Item();
        existingItem.setName("Old Smartphone");
        existingItem.setPrice(new BigDecimal("20000.00"));
        existingItem = itemRepository.save(existingItem);

        ItemDto updateDto = new ItemDto("New Smartphone Pro", new BigDecimal("85000.00"));
        String requestJson = objectMapper.writeValueAsString(updateDto);

        CurrentUser currentUser = new CurrentUser("user@gmail.com", userId);


        mockMvc.perform(put("/app/admin-order/update-item/{itemId}", existingItem.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(
                                        currentUser,
                                        null,
                                        Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                )
                        )))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItem() throws Exception {
        Long userId = 1L;

        Item itemToDelete = new Item();
        itemToDelete.setName("Item to Delete");
        itemToDelete.setPrice(new BigDecimal("100.00"));
        itemToDelete = itemRepository.save(itemToDelete);

        CurrentUser currentUser = new CurrentUser("user@gmail.com", userId);

        mockMvc.perform(delete("/app/admin-order/delete-item/{itemId}", itemToDelete.getId())
                        .with(SecurityMockMvcRequestPostProcessors.authentication(
                                new UsernamePasswordAuthenticationToken(
                                        currentUser,
                                        null,
                                        Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                )
                        )))
                .andExpect(status().isNoContent());

        assertThat(itemRepository.findById(itemToDelete.getId())).isEmpty();
    }




}
