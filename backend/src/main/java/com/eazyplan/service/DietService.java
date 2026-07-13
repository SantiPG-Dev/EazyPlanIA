package com.eazyplan.service;

import com.eazyplan.common.NotFoundException;
import com.eazyplan.domain.entities.Diet;
import com.eazyplan.domain.entities.Diet.DietType;
import com.eazyplan.domain.entities.User;
import com.eazyplan.repository.DietRepository;
import com.eazyplan.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de dietas migrada del POJO legado a bean Spring. */
@Service
@Transactional
public class DietService {

    private final DietRepository dietRepository;
    private final UserRepository userRepository;

    public DietService(DietRepository dietRepository, UserRepository userRepository) {
        this.dietRepository = dietRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Diet> getUserDiets(Long userId) {
        return dietRepository.findAllByUserIdOrderByStartDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public Diet getDiet(Long dietId) {
        return dietRepository.findById(dietId)
                .orElseThrow(() -> new NotFoundException("Diet", dietId));
    }

    public Diet createDiet(Diet diet, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        diet.setUser(user);
        return dietRepository.save(diet);
    }

    public Diet updateDiet(Long dietId, Diet changes) {
        Diet diet = getDiet(dietId);
        if (changes.getName() != null) diet.setName(changes.getName());
        if (changes.getDescription() != null) diet.setDescription(changes.getDescription());
        if (changes.getDietType() != null) diet.setDietType(changes.getDietType());
        if (changes.getStartDate() != null) diet.setStartDate(changes.getStartDate());
        if (changes.getEndDate() != null) diet.setEndDate(changes.getEndDate());
        if (changes.getDailyCalories() > 0) diet.setDailyCalories(changes.getDailyCalories());
        if (changes.getDailyProtein() >= 0) diet.setDailyProtein(changes.getDailyProtein());
        if (changes.getDailyCarbs() >= 0) diet.setDailyCarbs(changes.getDailyCarbs());
        if (changes.getDailyFats() >= 0) diet.setDailyFats(changes.getDailyFats());
        if (changes.getDailyWater() > 0) diet.setDailyWater(changes.getDailyWater());
        return dietRepository.save(diet);
    }

    public void deleteDiet(Long dietId) {
        getDiet(dietId); // valida existencia → 404 si no existe
        dietRepository.deleteById(dietId);
    }

    /** Reparte macros según el tipo de dieta sobre un objetivo calórico (lógica del legado). */
    @Transactional(readOnly = true)
    public Diet calculateMacros(DietType type, float targetCalories) {
        Diet d = new Diet();
        d.setDietType(type);
        switch (type) {
            case BALANCED -> { d.setDailyCalories(targetCalories); d.setDailyProtein(targetCalories * 0.30f); d.setDailyCarbs(targetCalories * 0.40f); d.setDailyFats(targetCalories * 0.30f); }
            case LOW_CARBS -> { d.setDailyCalories(targetCalories); d.setDailyProtein(targetCalories * 0.40f); d.setDailyCarbs(targetCalories * 0.20f); d.setDailyFats(targetCalories * 0.40f); }
            case HIGH_PROTEIN -> { d.setDailyCalories(targetCalories); d.setDailyProtein(targetCalories * 0.45f); d.setDailyCarbs(targetCalories * 0.25f); d.setDailyFats(targetCalories * 0.30f); }
            case KETO -> { d.setDailyCalories(targetCalories); d.setDailyProtein(targetCalories * 0.25f); d.setDailyCarbs(targetCalories * 0.05f); d.setDailyFats(targetCalories * 0.70f); }
            case VEGAN -> { d.setDailyCalories(targetCalories); d.setDailyProtein(targetCalories * 0.25f); d.setDailyCarbs(targetCalories * 0.45f); d.setDailyFats(targetCalories * 0.30f); }
            case CUSTOM -> d.setDailyCalories(targetCalories);
        }
        return d;
    }
}
