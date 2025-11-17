package com.example.workfusion;

import java.sql.Timestamp;

public class Seeker {
    private int id;
    private String name;
    private String email;
    private String skills;
    private int createdBy;
    private Timestamp createdAt;

    public Seeker() {}

    public Seeker(int id, String name, String email, String skills, int createdBy, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.skills = skills;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Seeker(String name, String email, String skills, int createdBy) {
        this.name = name;
        this.email = email;
        this.skills = skills;
        this.createdBy = createdBy;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Seeker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", skills='" + skills + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}