package com.eazyplan.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "micro_logs")
@NamedQueries({
    @NamedQuery(name = "microLog.findAllByUser", query = "SELECT m FROM MicroLog m WHERE m.user.id = :userId ORDER BY m.date DESC")
})
public class MicroLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate date;
    private double sodium;
    private double potassium;
    private double magnesium;
    private double calcium;
    private double iron;
    private double vitaminC;

    public MicroLog() {}

    public MicroLog(User user, LocalDate date) {
        this.user = user;
        this.date = date;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public double getSodium() { return sodium; }
    public void setSodium(double sodium) { this.sodium = sodium; }
    public double getPotassium() { return potassium; }
    public void setPotassium(double potassium) { this.potassium = potassium; }
    public double getMagnesium() { return magnesium; }
    public void setMagnesium(double magnesium) { this.magnesium = magnesium; }
    public double getCalcium() { return calcium; }
    public void setCalcium(double calcium) { this.calcium = calcium; }
    public double getIron() { return iron; }
    public void setIron(double iron) { this.iron = iron; }
    public double getVitaminC() { return vitaminC; }
    public void setVitaminC(double vitaminC) { this.vitaminC = vitaminC; }
}
