package com.eazypian.domain.services;

import com.eazypian.domain.entities.GroceryItem;
import com.eazypian.domain.entities.GroceryList;
import com.eazypian.domain.repositories.GroceryItemRepository;
import com.eazypian.domain.repositories.GroceryListRepository;
import com.eazypian.infrastructure.database.DatabaseConfig;

import java.util.List;
import java.time.LocalDate;

public class GroceryListService {
    private final GroceryListRepository groceryRepo = GroceryListRepository.get();
    private final GroceryItemRepository itemRepo = GroceryItemRepository.get();

    public List<GroceryList> getUserLists(Long userId) {
        return groceryRepo.findAllByUser(userId);
    }

    public GroceryList createList(GroceryList list, User user) {
        if (user != null) list.setUser(user);
        else throw new IllegalArgumentException("User required");
        list.setCreatedAt(LocalDate.now());
        groceryRepo.save(list);
        return list;
    }

    public void addItem(GroceryItem item) {
        itemRepo.save(item);
    }

    public void deleteList(GroceryList list) {
        // Delete items first, then the list
        for (GroceryItem item : list.getItems()) {
            itemRepo.delete(item);
        }
        groceryRepo.delete(list);
    }

    public void markPurchased(Long listId) {
        GroceryList list = groceryRepo.findById(listId);
        list.setPurchased(true);
        groceryRepo.save(list);
    }
}
