package com.example.workfusion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDao {

    /**
     * Create a new item in the database
     * 
     * @param item The item to create
     * @return The created item with generated ID
     * @throws SQLException if there's a database error
     */
    public Item create(Item item) throws SQLException {
        String sql = "INSERT INTO items (name, description, quantity) VALUES (?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setInt(3, item.getQuantity());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setId(rs.getInt(1));
                }
            }
        }
        return item;
    }

    /**
     * Read an item by ID
     * 
     * @param id The ID of the item to read
     * @return The item if found, null otherwise
     * @throws SQLException if there's a database error
     */
    public Item read(int id) throws SQLException {
        String sql = "SELECT id, name, description, quantity, created_at FROM items WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("quantity"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Read all items from the database
     * 
     * @return List of all items
     * @throws SQLException if there's a database error
     */
    public List<Item> readAll() throws SQLException {
        List<Item> list = new ArrayList<>();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT id, name, description, quantity, created_at FROM items ORDER BY name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Item(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getInt("quantity"),
                    rs.getTimestamp("created_at")
                ));
            }
        }
        return list;
    }

    /**
     * Update an existing item
     * 
     * @param item The item to update
     * @return true if update was successful, false otherwise
     * @throws SQLException if there's a database error
     */
    public boolean update(Item item) throws SQLException {
        String sql = "UPDATE items SET name = ?, description = ?, quantity = ? WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setInt(3, item.getQuantity());
            ps.setInt(4, item.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Delete an item by ID
     * 
     * @param id The ID of the item to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if there's a database error
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}