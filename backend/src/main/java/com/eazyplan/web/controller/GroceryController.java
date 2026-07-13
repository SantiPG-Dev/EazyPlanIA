package com.eazyplan.web.controller;

import com.eazyplan.service.GroceryListService;
import com.eazyplan.web.dto.GroceryItemRequest;
import com.eazyplan.web.dto.GroceryItemResponse;
import com.eazyplan.web.dto.GroceryListResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * API REST de listas de la compra.
 *
 * <pre>
 * GET    /api/users/{userId}/grocery-lists            → 200 [GroceryListResponse]
 * POST   /api/users/{userId}/grocery-lists            → 201 GroceryListResponse
 * GET    /api/grocery-lists/{listId}                  → 200 GroceryListResponse
 * GET    /api/grocery-lists/{listId}/items            → 200 [GroceryItemResponse]
 * POST   /api/grocery-lists/{listId}/items            → 201 GroceryItemResponse
 * PATCH  /api/grocery-lists/{listId}/purchase         → 200 GroceryListResponse
 * DELETE /api/grocery-lists/{listId}                  → 204
 * </pre>
 */
@RestController
@RequestMapping("/api")
public class GroceryController {

    private final GroceryListService groceryListService;

    public GroceryController(GroceryListService groceryListService) {
        this.groceryListService = groceryListService;
    }

    @GetMapping("/users/{userId}/grocery-lists")
    public List<GroceryListResponse> listUserLists(@PathVariable Long userId) {
        return groceryListService.getUserLists(userId).stream()
                .map(GroceryListResponse::from)
                .toList();
    }

    @PostMapping("/users/{userId}/grocery-lists")
    public ResponseEntity<GroceryListResponse> createList(@PathVariable Long userId) {
        var created = groceryListService.createList(userId);
        return ResponseEntity
                .created(URI.create("/api/grocery-lists/" + created.getId()))
                .body(GroceryListResponse.from(created));
    }

    @GetMapping("/grocery-lists/{listId}")
    public GroceryListResponse getList(@PathVariable Long listId) {
        return GroceryListResponse.from(groceryListService.getList(listId));
    }

    @GetMapping("/grocery-lists/{listId}/items")
    public List<GroceryItemResponse> listItems(@PathVariable Long listId) {
        return groceryListService.getListItems(listId).stream()
                .map(GroceryItemResponse::from)
                .toList();
    }

    @PostMapping("/grocery-lists/{listId}/items")
    public ResponseEntity<GroceryItemResponse> addItem(@PathVariable Long listId,
                                                        @Valid @RequestBody GroceryItemRequest request) {
        var saved = groceryListService.addItem(listId, request.toEntity());
        return ResponseEntity
                .created(URI.create("/api/grocery-lists/" + listId + "/items/" + saved.getId()))
                .body(GroceryItemResponse.from(saved));
    }

    @PatchMapping("/grocery-lists/{listId}/purchase")
    public GroceryListResponse markPurchased(@PathVariable Long listId) {
        return GroceryListResponse.from(groceryListService.markPurchased(listId));
    }

    @DeleteMapping("/grocery-lists/{listId}")
    public ResponseEntity<Void> deleteList(@PathVariable Long listId) {
        groceryListService.deleteList(listId);
        return ResponseEntity.noContent().build();
    }
}
