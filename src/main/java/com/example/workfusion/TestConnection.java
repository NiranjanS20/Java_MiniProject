package com.example.workfusion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        try (Connection conn = Database.getConnection()) {
            System.out.println("Connected to database successfully!");
            
            // Test simple query
            try (PreparedStatement stmt = conn.prepareStatement("SELECT 1")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Simple query result: " + rs.getInt(1));
                }
            }
            
            // Test jobs table
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM jobs")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Number of jobs in database: " + rs.getInt(1));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}