package com.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.dto.ItemDto;
import com.entity.Item;
import com.repository.ItemRepository;
import com.service.impl.ItemServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private Item testItem;
    private ItemDto testDto;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Test Item");
        testItem.setPrice(new BigDecimal("100.00"));

        testDto = new ItemDto();
        testDto.setName("New Name");
        testDto.setPrice(new BigDecimal("200.00"));
    }

    @Test
    void createItem_Success() {
        // Arrange
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        // Act
        Item result = itemService.createItem(testDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Item", result.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem_Success() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Item result = itemService.updateItem(testDto, 1L);

        // Assert
        assertEquals("New Name", result.getName());
        assertEquals(new BigDecimal("200.00"), result.getPrice());
        verify(itemRepository).save(testItem);
    }

    @Test
    void updateItem_NotFound() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            itemService.updateItem(testDto, 1L);
        });

        assertTrue(exception.getMessage().contains("is not found"));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getAllItems_Success() {
        // Arrange
        List<Item> items = Arrays.asList(testItem, new Item());
        when(itemRepository.findAll()).thenReturn(items);

        // Act
        List<Item> result = itemService.getAllItems();

        // Assert
        assertEquals(2, result.size());
        verify(itemRepository).findAll();
    }

    @Test
    void deleteItem_Success() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        // Act
        itemService.deleteItem(1L);

        // Assert
        verify(itemRepository, times(1)).delete(testItem);
    }

    @Test
    void deleteItem_NotFound() {
        // Arrange
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            itemService.deleteItem(1L);
        });

        verify(itemRepository, never()).delete(any());
    }
}
