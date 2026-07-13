package com.eazyplan.service;

import com.eazyplan.common.NotFoundException;
import com.eazyplan.domain.entities.Diet;
import com.eazyplan.domain.entities.Diet.DietType;
import com.eazyplan.domain.entities.User;
import com.eazyplan.repository.DietRepository;
import com.eazyplan.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios de {@link DietService} con repositorios mockados.
 * Sin contexto Spring, sin base de datos: verifican lógica de negocio pura.
 */
@ExtendWith(MockitoExtension.class)
class DietServiceTest {

    @Mock DietRepository dietRepository;
    @Mock UserRepository userRepository;
    @InjectMocks DietService dietService;

    private User sampleUser() {
        User u = new User("dieter", "Dieter", "d@e.com", "hash");
        u.setId(1L);
        return u;
    }

    @Test
    void createDiet_setsUserAndSaves() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser()));
        Diet diet = new Diet();
        diet.setName("Keto");
        diet.setDietType(DietType.KETO);

        dietService.createDiet(diet, 1L);

        assertEquals(1L, diet.getUser().getId());
        verify(dietRepository).save(diet);
    }

    @Test
    void createDiet_throwsNotFound_whenUserMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> dietService.createDiet(new Diet(), 99L));
        verify(dietRepository, never()).save(any());
    }

    @Test
    void updateDiet_appliesPartialMerge() {
        Diet existing = new Diet();
        existing.setId(1L);
        existing.setName("Old");
        existing.setDailyCalories(1000);
        when(dietRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(dietRepository.save(existing)).thenReturn(existing);

        Diet changes = new Diet();
        changes.setName("New Name");
        changes.setDailyCalories(0); // 0 → no se actualiza (>0 check)
        dietService.updateDiet(1L, changes);

        assertEquals("New Name", existing.getName());
        assertEquals(1000, existing.getDailyCalories()); // no cambiada
    }

    @Test
    void deleteDiet_validatesExistenceBeforeDelete() {
        when(dietRepository.findById(1L)).thenReturn(Optional.of(new Diet()));

        dietService.deleteDiet(1L);

        verify(dietRepository).deleteById(1L);
    }

    @Test
    void deleteDiet_throwsNotFound_whenMissing() {
        when(dietRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> dietService.deleteDiet(99L));
        verify(dietRepository, never()).deleteById(any());
    }

    @Test
    void calculateMacros_balanced_30_40_30_split() {
        Diet result = dietService.calculateMacros(DietType.BALANCED, 2000);

        assertEquals(2000, result.getDailyCalories());
        assertEquals(600, result.getDailyProtein());   // 30%
        assertEquals(800, result.getDailyCarbs());     // 40%
        assertEquals(600, result.getDailyFats());      // 30%
    }

    @Test
    void calculateMacros_keto_lowCarbs() {
        Diet result = dietService.calculateMacros(DietType.KETO, 2000);

        assertEquals(100, result.getDailyCarbs());     // 5%
        assertEquals(1400, result.getDailyFats());     // 70%
    }

    @Test
    void getDiet_throwsNotFound_whenMissing() {
        when(dietRepository.findById(42L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> dietService.getDiet(42L));
    }
}
