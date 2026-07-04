package com.eazypian.domain.services;

import com.eazypian.domain.entities.Diet;
import com.eazypian.domain.repositories.DietRepository;
import java.util.List;

public class DietService {
    private final DietRepository dietRepo = DietRepository.get();

    public List<Diet> getUserDiets(Long userId) { return dietRepo.findAllByUser(userId); }
    public Diet createDiet(Diet diet, UserService userService, Long userId) { 
        if (userService != null && userId != null) diet.setUser(userService.findById(userId));
        dietRepo.save(diet); return diet; 
    }
    public void updateDiet(Diet diet, String name, String description, Diet.DietType type, float cal, float protein, float carbs, float fats, float water) {
        if (name != null) diet.setName(name);
        if (description != null) diet.setDescription(description);
        if (type != null) diet.setDietType(type);
        if (cal > 0) diet.setDailyCalories(cal);
        if (protein >= 0) diet.setDailyProtein(protein);
        if (carbs >= 0) diet.setDailyCarbs(carbs);
        if (fats >= 0) diet.setDailyFats(fats);
        if (water > 0) diet.setDailyWater(water);
        dietRepo.save(diet);
    }
    public void deleteDiet(Diet diet) { dietRepo.delete(diet); }
    public Diet calculateMacros(Diet.DietType type, float targetCalories) {
        Diet d = new Diet();
        switch (type) {
            case BALANCED: d.setDailyCalories(targetCalories); d.setDailyProtein((float)(targetCalories*0.30)); d.setDailyCarbs((float)(targetCalories*0.40)); d.setDailyFats((float)(targetCalories*0.30)); break;
            case LOW_CARBS: d.setDailyCalories(targetCalories); d.setDailyProtein((float)(targetCalories*0.40)); d.setDailyCarbs((float)(targetCalories*0.20)); d.setDailyFats((float)(targetCalories*0.40)); break;
            case HIGH_PROTEIN: d.setDailyCalories(targetCalories); d.setDailyProtein((float)(targetCalories*0.45)); d.setDailyCarbs((float)(targetCalories*0.25)); d.setDailyFats((float)(targetCalories*0.30)); break;
            case KETO: d.setDailyCalories(targetCalories); d.setDailyProtein((float)(targetCalories*0.25)); d.setDailyCarbs((float)(targetCalories*0.05)); d.setDailyFats((float)(targetCalories*0.70)); break;
            case VEGAN: d.setDailyCalories(targetCalories); d.setDailyProtein((float)(targetCalories*0.25)); d.setDailyCarbs((float)(targetCalories*0.45)); d.setDailyFats((float)(targetCalories*0.30)); break;
        }
        return d;
    }
}
