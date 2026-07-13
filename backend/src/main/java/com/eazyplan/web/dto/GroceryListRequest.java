package com.eazyplan.web.dto;

/**
 * Cuerpo de {@code POST /api/users/{userId}/grocery-lists}.
 *
 * <p>La entidad {@link com.eazyplan.domain.entities.GroceryList} no tiene campo
 * nombre (solo user, items, createdAt, isPurchased), por lo que la petición
 * puede ir vacía y simplemente crea una lista nueva para el usuario.
 */
public record GroceryListRequest() {}
