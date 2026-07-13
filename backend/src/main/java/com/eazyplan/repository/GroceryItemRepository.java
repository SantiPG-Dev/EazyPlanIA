package com.eazyplan.repository;

import com.eazyplan.domain.entities.GroceryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {
    List<GroceryItem> findAllByGroceryListIdOrderByName(Long groceryListId);
}
