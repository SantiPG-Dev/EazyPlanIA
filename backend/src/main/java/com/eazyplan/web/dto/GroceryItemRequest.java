package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.GroceryItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** Cuerpo de {@code POST /api/grocery-lists/{listId}/items}. */
public record GroceryItemRequest(
        String name,
        String category,
        @Min(0) int quantity,
        String unit,
        boolean organic
) {
    public GroceryItem toEntity() {
        GroceryItem item = new GroceryItem();
        item.setName(name);
        item.setCategory(category);
        item.setQuantity(quantity);
        item.setUnit(unit);
        item.setOrganic(organic);
        return item;
    }
}
