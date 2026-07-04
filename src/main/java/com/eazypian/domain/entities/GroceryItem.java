package com.eazypian.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "grocery_items")
public class GroceryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grocery_list_id", nullable = false)
    private GroceryList groceryList;

    private String name;
    private String category;
    private int quantity;
    private String unit; // e.g., kg, L, pieces
    private boolean purchased;
    private boolean organic;

    public GroceryItem() {}

    public GroceryItem(String name, String category, int quantity, String unit) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
    }

    @NamedQuery(name = "groceryItem.findAllByList", query = "SELECT gi FROM GroceryItem gi WHERE gi.groceryList.id = :listId ORDER BY gi.name")

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GroceryList getGroceryList() { return groceryList; }
    public void setGroceryList(GroceryList groceryList) { this.groceryList = groceryList; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public boolean isPurchased() { return purchased; }
    public void setPurchased(boolean purchased) { this.purchased = purchased; }
    public boolean isOrganic() { return organic; }
    public void setOrganic(boolean organic) { this.organic = organic; }
}
