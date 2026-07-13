package com.eazyplan.service;

import com.eazyplan.common.NotFoundException;
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

    @Transactional(readOnly = true)
    public List<GroceryList> getUserLists(Long userId) {
        return groceryListRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public GroceryList getList(Long listId) {
        return groceryListRepository.findWithItemsById(listId)
                .orElseThrow(() -> new NotFoundException("GroceryList", listId));
    }

    @Transactional(readOnly = true)
    public List<GroceryItem> getListItems(Long listId) {
        return groceryItemRepository.findAllByGroceryListIdOrderByName(listId);
    }

    public GroceryList createList(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        GroceryList list = new GroceryList(user);
        list.setCreatedAt(LocalDate.now());
        return groceryListRepository.save(list);
    }

    public GroceryItem addItem(Long listId, GroceryItem item) {
        GroceryList list = getList(listId);
        item.setGroceryList(list);
        return groceryItemRepository.save(item);
    }

    /** orphanRemoval en la entidad elimina los items en cascada. */
    public void deleteList(Long listId) {
        getList(listId);
        groceryListRepository.deleteById(listId);
    }

    public GroceryList markPurchased(Long listId) {
        GroceryList list = getList(listId);
        list.setPurchased(true);
        return groceryListRepository.save(list);
    }
}
