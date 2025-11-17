package com.example.workfusion;

import java.sql.Timestamp;

public class Item {
    private int id;
    private String name;
    private String description;
    private int quantity;
    private Timestamp createdAt;

    public Item() {}

    public Item(int id, String name, String description, int quantity, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public Item(String name, String description, int quantity) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                '}';
    }
}
