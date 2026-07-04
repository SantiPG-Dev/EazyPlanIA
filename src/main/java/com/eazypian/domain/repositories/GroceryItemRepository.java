package com.eazyplan.ia.domain.repositories;

import com.eazyplan.ia.domain.entities.GroceryItem;

import java.util.List;

public interface GroceryItemRepository {
    List<GroceryItem> findAllByList(Long listId);
    List<GroceryItem> findByIds(List<Long> ids);
    GroceryItem findById(Long id);
    void save(GroceryItem groceryItem);
    void delete(GroceryItem groceryItem);
}
