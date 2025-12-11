package com.service.impl;

import com.dto.ItemDto;
import com.entity.Item;
import com.repository.ItemRepository;
import com.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public Item createItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(ItemDto itemDto, long idItem) {

        Item item = itemRepository.findById(idItem)
                .orElseThrow(() -> new EntityNotFoundException("Item with id= "+ idItem + " is not found"));

        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());
        return itemRepository.save(item);
    }


    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public void deleteItem(long idItem) {
        Item item = itemRepository.findById(idItem)
                .orElseThrow(() -> new EntityNotFoundException("Item with id= "+ idItem + " is not found"));

        itemRepository.delete(item);
    }
}
