package com.eazyplan.repository;

import com.eazyplan.domain.entities.GroceryList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroceryListRepository extends JpaRepository<GroceryList, Long> {
    List<GroceryList> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
