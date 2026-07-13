package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.GroceryItem;

/** Respuesta pública de un item de compra. */
public record GroceryItemResponse(
        Long id,
        Long groceryListId,
        String name,
        String category,
        int quantity,
        String unit,
        boolean purchased,
        boolean organic
) {
    public static GroceryItemResponse from(GroceryItem i) {
        return new GroceryItemResponse(
                i.getId(),
                i.getGroceryList().getId(),
                i.getName(),
                i.getCategory(),
                i.getQuantity(),
                i.getUnit(),
                i.isPurchased(),
                i.isOrganic());
    }
}
