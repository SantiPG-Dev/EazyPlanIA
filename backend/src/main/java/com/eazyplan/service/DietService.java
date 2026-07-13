package com.eazyplan.service;

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

    public List<Diet> getUserDiets(Long userId) {
        return dietRepository.findAllByUserIdOrderByStartDateDesc(userId);
    }

    public Diet createDiet(Diet diet, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        diet.setUser(user);
        return dietRepository.save(diet);
    }

    public Diet updateDiet(Long dietId, String name, String description, DietType type,
                           float cal, float protein, float carbs, float fats, float water) {
        Diet diet = dietRepository.findById(dietId)
                .orElseThrow(() -> new IllegalArgumentException("Diet not found: " + dietId));
        if (name != null) diet.setName(name);
        if (description != null) diet.setDescription(description);
        if (type != null) diet.setDietType(type);
        if (cal > 0) diet.setDailyCalories(cal);
        if (protein >= 0) diet.setDailyProtein(protein);
        if (carbs >= 0) diet.setDailyCarbs(carbs);
        if (fats >= 0) diet.setDailyFats(fats);
        if (water > 0) diet.setDailyWater(water);
        return dietRepository.save(diet);
    }

    public void deleteDiet(Long dietId) {
        dietRepository.deleteById(dietId);
    }

    /** Reparte macros según el tipo de dieta sobre un objetivo calórico (lógica del legado). */
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
