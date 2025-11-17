package com.example.workfusion.dao;

import com.example.workfusion.Database;
import com.example.workfusion.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    /**
     * Create a new user in the database
     * 
     * @param u The user to create
     * @return The created user with generated id
     * @throws SQLException if there's a database error
     */
    public User create(User u) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getRole());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }
        }
        return u;
    }

    /**
     * Read a user by ID
     * 
     * @param id The ID of the user to read
     * @return Optional containing the user if found, empty otherwise
     * @throws SQLException if there's a database error
     */
    public Optional<User> read(int id) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, created_at FROM users WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement pstmt = c.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setRole(rs.getString("role"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return Optional.of(user);
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Read all users from the database
     * 
     * @return List of all users
     * @throws SQLException if there's a database error
     */
    public List<User> readAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, role, created_at FROM users ORDER BY username";
        
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                users.add(user);
            }
            
            System.out.println("DEBUG: Loaded " + users.size() + " users from DB");
        }
        
        return users;
    }

    /**
     * Update an existing user
     * 
     * @param u The user to update
     * @return true if update was successful, false otherwise
     * @throws SQLException if there's a database error
     */
    public boolean update(User u) throws SQLException {
        String sql = "UPDATE users SET username = ?, password_hash = ?, role = ? WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getUsername());
            pstmt.setString(2, u.getPasswordHash());
            pstmt.setString(3, u.getRole());
            pstmt.setInt(4, u.getId());
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete a user by ID
     * 
     * @param id The ID of the user to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if there's a database error
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}