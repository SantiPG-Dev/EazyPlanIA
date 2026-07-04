package com.eazyplan.ia.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "macro_logs")
public class MacroLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diet_id", nullable = false)
    private Diet diet;

    private LocalDate date;
    private double calories;
    private double protein;
    private double carbs;
    private double fats;

    public MacroLog() {}

    public MacroLog(User user, Diet diet, LocalDate date) {
        this.user = user;
        this.diet = diet;
        this.date = date;
    }

    @NamedQuery(name = "macroLog.findAllByUser", query = "SELECT m FROM MacroLog m WHERE m.user.id = :userId ORDER BY m.date DESC")

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Diet getDiet() { return diet; }
    public void setDiet(Diet diet) { this.diet = diet; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }
    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }
    public double getCarbs() { return carbs; }
    public void setCarbs(double carbs) { this.carbs = carbs; }
    public double getFats() { return fats; }
    public void setFats(double fats) { this.fats = fats; }
}
