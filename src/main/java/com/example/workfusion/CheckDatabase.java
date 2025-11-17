package com.example.workfusion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class CheckDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/job_matching";
        String user = "root";
        String password = "Jaya98765!";
        
        try {
            System.out.println("Attempting to connect to database...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Successfully connected to the database!");
            
            // Check jobs table
            System.out.println("\n--- Jobs Table ---");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM jobs LIMIT 10");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + 
                                 ", Title: " + rs.getString("title") + 
                                 ", Skills: " + rs.getString("skills"));
            }
            
            // Check seekers table
            System.out.println("\n--- Seekers Table ---");
            rs = stmt.executeQuery("SELECT * FROM seekers LIMIT 10");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + 
                                 ", Name: " + rs.getString("name") + 
                                 ", Skills: " + rs.getString("skills"));
            }
            
            connection.close();
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}