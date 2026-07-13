package com.eazyplan.domain.entities;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "diets")
@NamedQuery(name = "diet.findAllByUser", query = "SELECT d FROM Diet d WHERE d.user.id = :userId ORDER BY d.startDate DESC")
public class Diet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DietType dietType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private float dailyCalories;

    @Column
    private float dailyProtein;

    @Column
    private float dailyCarbs;

    @Column
    private float dailyFats;

    @Column
    private float dailyWater;

    public Diet() {}

    public Diet(User user, String name, DietType dietType, LocalDate startDate, float dailyCalories,
                 float dailyProtein, float dailyCarbs, float dailyFats, float dailyWater) {
        this.user = user;
        this.name = name;
        this.dietType = dietType;
        this.startDate = startDate;
        this.dailyCalories = dailyCalories;
        this.dailyProtein = dailyProtein;
        this.dailyCarbs = dailyCarbs;
        this.dailyFats = dailyFats;
        this.dailyWater = dailyWater;
    }

    public enum DietType {
        BALANCED, LOW_CARBS, HIGH_PROTEIN, VEGAN, KETO, CUSTOM
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public DietType getDietType() { return dietType; }
    public void setDietType(DietType dietType) { this.dietType = dietType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public float getDailyCalories() { return dailyCalories; }
    public void setDailyCalories(float dailyCalories) { this.dailyCalories = dailyCalories; }
    public float getDailyProtein() { return dailyProtein; }
    public void setDailyProtein(float dailyProtein) { this.dailyProtein = dailyProtein; }
    public float getDailyCarbs() { return dailyCarbs; }
    public void setDailyCarbs(float dailyCarbs) { this.dailyCarbs = dailyCarbs; }
    public float getDailyFats() { return dailyFats; }
    public void setDailyFats(float dailyFats) { this.dailyFats = dailyFats; }
    public float getDailyWater() { return dailyWater; }
    public void setDailyWater(float dailyWater) { this.dailyWater = dailyWater; }
}
