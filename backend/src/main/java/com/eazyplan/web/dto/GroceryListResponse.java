package com.eazyplan.web.dto;

import com.eazyplan.domain.entities.GroceryList;

import java.time.LocalDate;
import java.util.List;

/** Respuesta pública de una lista de la compra con sus items. */
public record GroceryListResponse(
        Long id,
        Long userId,
        LocalDate createdAt,
        boolean purchased,
        List<GroceryItemResponse> items
) {
    public static GroceryListResponse from(GroceryList g) {
        List<GroceryItemResponse> items = g.getItems().stream()
                .map(GroceryItemResponse::from)
                .toList();
        return new GroceryListResponse(
                g.getId(),
                g.getUser().getId(),
                g.getCreatedAt(),
                g.isPurchased(),
                items);
    }
}
