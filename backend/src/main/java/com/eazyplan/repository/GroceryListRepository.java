package com.eazyplan.repository;

import com.eazyplan.domain.entities.GroceryList;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroceryListRepository extends JpaRepository<GroceryList, Long> {

    @EntityGraph(attributePaths = "items")
    List<GroceryList> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    /** Devuelve la lista con sus items ya cargados (evita LazyInit con open-in-view=false). */
    @EntityGraph(attributePaths = "items")
    Optional<GroceryList> findWithItemsById(Long id);
}
