package com.service;

import com.dto.ItemDto;
import com.entity.Item;

import java.util.List;

public interface ItemService {

    Item createItem(ItemDto itemDTO);

    Item updateItem (ItemDto itemDto, long idItem);

    List<Item> getAllItems ();

    void deleteItem (long idItem);
}
