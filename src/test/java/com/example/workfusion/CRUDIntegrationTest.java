package com.example.workfusion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

public class CRUDIntegrationTest {

    @Test
    @DisplayName("Full CRUD Integration Test")
    public void testFullCRUDIntegration() throws SQLException {
        ItemDao itemDao = new ItemDao();
        
        // 1. Create - Test creating a new item
        Item newItem = new Item("Integration Test Item", "This is a test item for integration testing", 42);
        Item createdItem = itemDao.create(newItem);
        
        // Verify item was created successfully
        assertNotNull(createdItem, "Created item should not be null");
        assertNotNull(createdItem.getId(), "Created item should have an ID");
        assertTrue(createdItem.getId() > 0, "Created item ID should be positive");
        assertEquals("Integration Test Item", createdItem.getName());
        assertEquals("This is a test item for integration testing", createdItem.getDescription());
        assertEquals(42, createdItem.getQuantity());
        
        int itemId = createdItem.getId();
        
        // 2. Read - Test reading the created item by ID
        Item readItem = itemDao.read(itemId);
        
        // Verify item was read correctly
        assertNotNull(readItem, "Read item should not be null");
        assertEquals(itemId, readItem.getId());
        assertEquals("Integration Test Item", readItem.getName());
        assertEquals("This is a test item for integration testing", readItem.getDescription());
        assertEquals(42, readItem.getQuantity());
        
        // 3. Read All - Test reading all items
        var allItems = itemDao.readAll();
        
        // Verify we have at least the item we created
        assertNotNull(allItems, "All items list should not be null");
        assertFalse(allItems.isEmpty(), "All items list should not be empty");
        assertTrue(allItems.size() >= 1, "Should have at least one item");
        
        // Check that our item is in the list
        boolean foundItem = false;
        for (Item item : allItems) {
            if (item.getId() == itemId) {
                foundItem = true;
                break;
            }
        }
        assertTrue(foundItem, "Created item should be in the list of all items");
        
        // 4. Update - Test updating the item
        createdItem.setName("Updated Integration Test Item");
        createdItem.setDescription("This item has been updated");
        createdItem.setQuantity(99);
        
        boolean updated = itemDao.update(createdItem);
        
        // Verify update was successful
        assertTrue(updated, "Update should be successful");
        
        // Read updated item to verify changes
        Item updatedItem = itemDao.read(itemId);
        
        // Verify changes were applied
        assertNotNull(updatedItem, "Updated item should not be null");
        assertEquals("Updated Integration Test Item", updatedItem.getName());
        assertEquals("This item has been updated", updatedItem.getDescription());
        assertEquals(99, updatedItem.getQuantity());
        
        // 5. Delete - Test deleting the item
        boolean deleted = itemDao.delete(itemId);
        
        // Verify deletion was successful
        assertTrue(deleted, "Delete should be successful");
        
        // Try to read deleted item
        Item deletedItem = itemDao.read(itemId);
        
        // Verify item was actually deleted
        assertNull(deletedItem, "Deleted item should be null");
        
        System.out.println("Full CRUD integration test completed successfully!");
    }
}