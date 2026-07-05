package com.eazyplan.domain.services;

import com.eazyplan.domain.entities.GroceryItem;
import com.eazyplan.domain.entities.GroceryList;
import com.eazyplan.domain.repositories.GroceryItemRepository;
import com.eazyplan.domain.repositories.GroceryListRepository;
import com.eazyplan.infrastructure.database.DatabaseConfig;

import java.util.List;
import java.time.LocalDate;
import com.eazyplan.domain.repositories.GroceryListRepositoryImpl;
import com.eazyplan.domain.repositories.GroceryItemRepositoryImpl;
import com.eazyplan.domain.entities.User;

public class GroceryListService {
    private final GroceryListRepository groceryRepo = GroceryListRepositoryImpl.get();
    private final GroceryItemRepository itemRepo = GroceryItemRepositoryImpl.get();

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
