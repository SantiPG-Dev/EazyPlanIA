package com.eazyplan.common;

/**
 * Excepción de dominio lanzada cuando un recurso no existe.
 *
 * <p>Se traduce a HTTP 404 vía {@link com.eazyplan.web.advice.GlobalExceptionHandler}.
 * Desacopla los servicios de Spring Web (no hereda de {@code ResponseStatusException}).
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String resource, Object id) {
        super(resource + " not found: " + id);
    }
}
