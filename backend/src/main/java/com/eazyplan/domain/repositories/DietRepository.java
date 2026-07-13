package com.eazyplan.domain.repositories;

import com.eazyplan.domain.entities.Diet;

import java.util.List;

public interface DietRepository {
    List<Diet> findAllByUser(Long userId);
    List<Diet> findByIds(List<Long> ids);
    Diet findById(Long id);
    void save(Diet diet);
    void delete(Diet diet);
}
