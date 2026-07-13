package com.eazyplan.service;

import com.eazyplan.domain.entities.GroceryItem;
import com.eazyplan.domain.entities.GroceryList;
import com.eazyplan.domain.entities.User;
import com.eazyplan.repository.GroceryItemRepository;
import com.eazyplan.repository.GroceryListRepository;
import com.eazyplan.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class GroceryListService {

    private final GroceryListRepository groceryListRepository;
    private final GroceryItemRepository groceryItemRepository;
    private final UserRepository userRepository;

    public GroceryListService(GroceryListRepository groceryListRepository,
                              GroceryItemRepository groceryItemRepository,
                              UserRepository userRepository) {
        this.groceryListRepository = groceryListRepository;
        this.groceryItemRepository = groceryItemRepository;
        this.userRepository = userRepository;
    }

    public List<GroceryList> getUserLists(Long userId) {
        return groceryListRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    public GroceryList createList(GroceryList list, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        list.setUser(user);
        list.setCreatedAt(LocalDate.now());
        return groceryListRepository.save(list);
    }

    public GroceryItem addItem(GroceryItem item) {
        return groceryItemRepository.save(item);
    }

    /** orphanRemoval en la entidad elimina los items en cascada. */
    public void deleteList(Long listId) {
        groceryListRepository.deleteById(listId);
    }

    public GroceryList markPurchased(Long listId) {
        GroceryList list = groceryListRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("GroceryList not found: " + listId));
        list.setPurchased(true);
        return groceryListRepository.save(list);
    }
}
