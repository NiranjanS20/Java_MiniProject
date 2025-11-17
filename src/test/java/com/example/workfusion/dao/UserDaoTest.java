package com.example.workfusion.dao;

import com.example.workfusion.Database;
import com.example.workfusion.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserDaoTest {

    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        userDao = new UserDao();
    }

    @Test
    @DisplayName("Test create user")
    public void testCreateUser() throws SQLException {
        // Create user
        User user = new User("testuser", "hashedpassword", "seeker");
        User createdUser = userDao.create(user);
        
        // Verify user was created with ID
        assertNotNull(createdUser.getId());
        assertEquals("testuser", createdUser.getUsername());
        assertEquals("hashedpassword", createdUser.getPasswordHash());
        assertEquals("seeker", createdUser.getRole());
        
        // Clean up
        userDao.delete(createdUser.getId());
    }

    @Test
    @DisplayName("Test read user by ID")
    public void testReadUser() throws SQLException {
        // Create user first
        User user = new User("testuser2", "hashedpassword2", "employer");
        User createdUser = userDao.create(user);
        int userId = createdUser.getId();
        
        // Read user
        Optional<User> readUser = userDao.read(userId);
        
        // Verify user was read correctly
        assertTrue(readUser.isPresent());
        assertEquals(userId, readUser.get().getId());
        assertEquals("testuser2", readUser.get().getUsername());
        assertEquals("hashedpassword2", readUser.get().getPasswordHash());
        assertEquals("employer", readUser.get().getRole());
        
        // Clean up
        userDao.delete(userId);
    }

    @Test
    @DisplayName("Test read all users")
    public void testReadAllUsers() throws SQLException {
        // Get initial count
        List<User> initialUsers = userDao.readAll();
        int initialCount = initialUsers.size();
        
        // Create a couple of users
        User user1 = userDao.create(new User("user1", "hash1", "admin"));
        User user2 = userDao.create(new User("user2", "hash2", "seeker"));
        
        // Read all users
        List<User> users = userDao.readAll();
        
        // Verify we have more users now
        assertEquals(initialCount + 2, users.size());
        
        // Clean up
        userDao.delete(user1.getId());
        userDao.delete(user2.getId());
    }

    @Test
    @DisplayName("Test update user")
    public void testUpdateUser() throws SQLException {
        // Create user
        User user = new User("testuser3", "hashedpassword3", "seeker");
        User createdUser = userDao.create(user);
        int userId = createdUser.getId();
        
        // Update user
        createdUser.setUsername("updateduser");
        createdUser.setRole("admin");
        
        boolean updated = userDao.update(createdUser);
        
        // Verify update was successful
        assertTrue(updated);
        
        // Read updated user
        Optional<User> updatedUser = userDao.read(userId);
        
        // Verify changes
        assertTrue(updatedUser.isPresent());
        assertEquals("updateduser", updatedUser.get().getUsername());
        assertEquals("admin", updatedUser.get().getRole());
        
        // Clean up
        userDao.delete(userId);
    }

    @Test
    @DisplayName("Test delete user")
    public void testDeleteUser() throws SQLException {
        // Create user
        User user = new User("testuser4", "hashedpassword4", "employer");
        User createdUser = userDao.create(user);
        int userId = createdUser.getId();
        
        // Delete user
        boolean deleted = userDao.delete(userId);
        
        // Verify deletion was successful
        assertTrue(deleted);
        
        // Try to read deleted user
        Optional<User> readUser = userDao.read(userId);
        
        // Verify user was deleted
        assertFalse(readUser.isPresent());
    }
}