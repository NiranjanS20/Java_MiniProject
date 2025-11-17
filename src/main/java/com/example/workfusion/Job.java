package com.example.workfusion;

import java.sql.Timestamp;

public class Job {
    private int id;
    private String title;
    private String description;
    private String skills;
    private int createdBy;
    private Timestamp createdAt;

    public Job() {}

    public Job(int id, String title, String description, String skills, int createdBy, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public Job(String title, String description, String skills, int createdBy) {
        this.title = title;
        this.description = description;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", skills='" + skills + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }
}