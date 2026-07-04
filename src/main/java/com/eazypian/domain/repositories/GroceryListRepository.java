package com.eazypian.domain.repositories;

import com.eazypian.domain.entities.GroceryList;

import java.util.List;

public interface GroceryListRepository {
    List<GroceryList> findAllByUser(Long userId);
    List<GroceryList> findByIds(List<Long> ids);
    GroceryList findById(Long id);
    void save(GroceryList groceryList);
    void delete(GroceryList groceryList);
}
