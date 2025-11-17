package com.example.workfusion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

public class ItemDaoTest {

    private ItemDao itemDao;
    private Item testItem;

    @BeforeEach
    public void setUp() throws SQLException {
        itemDao = new ItemDao();
        testItem = new Item("Test Item", "Test Description", 5);
    }

    @Test
    @DisplayName("Test create item")
    public void testCreateItem() throws SQLException {
        // Create item
        Item createdItem = itemDao.create(testItem);
        
        // Verify item was created with ID
        assertNotNull(createdItem, "Created item should not be null");
        assertNotNull(createdItem.getId(), "Created item ID should not be null");
        assertEquals(testItem.getName(), createdItem.getName());
        assertEquals(testItem.getDescription(), createdItem.getDescription());
        assertEquals(testItem.getQuantity(), createdItem.getQuantity());
        // Note: createdAt is set by the database, so we don't check it here
        
        // Clean up
        if (createdItem.getId() > 0) {
            itemDao.delete(createdItem.getId());
        }
    }

    @Test
    @DisplayName("Test read item by ID")
    public void testReadItem() throws SQLException {
        // Create item first
        Item createdItem = itemDao.create(testItem);
        assertNotNull(createdItem, "Created item should not be null");
        int itemId = createdItem.getId();
        assertTrue(itemId > 0, "Item ID should be greater than 0");
        
        // Read item
        Item readItem = itemDao.read(itemId);
        
        // Verify item was read correctly
        assertNotNull(readItem, "Read item should not be null");
        assertEquals(itemId, readItem.getId());
        assertEquals(testItem.getName(), readItem.getName());
        assertEquals(testItem.getDescription(), readItem.getDescription());
        assertEquals(testItem.getQuantity(), readItem.getQuantity());
        
        // Clean up
        itemDao.delete(itemId);
    }

    @Test
    @DisplayName("Test read all items")
    public void testReadAllItems() throws SQLException {
        // Create a couple of items
        Item item1 = itemDao.create(new Item("Item 1", "Description 1", 10));
        Item item2 = itemDao.create(new Item("Item 2", "Description 2", 20));
        
        // Verify items were created
        assertNotNull(item1, "Item 1 should not be null");
        assertNotNull(item2, "Item 2 should not be null");
        assertTrue(item1.getId() > 0, "Item 1 ID should be greater than 0");
        assertTrue(item2.getId() > 0, "Item 2 ID should be greater than 0");
        
        // Read all items
        List<Item> items = itemDao.readAll();
        
        // Verify we have at least the items we created
        assertTrue(items.size() >= 2);
        
        // Check that our items are in the list
        boolean foundItem1 = false;
        boolean foundItem2 = false;
        
        for (Item item : items) {
            if (item.getId() == item1.getId()) {
                foundItem1 = true;
            }
            if (item.getId() == item2.getId()) {
                foundItem2 = true;
            }
        }
        
        assertTrue(foundItem1, "Item 1 should be in the list");
        assertTrue(foundItem2, "Item 2 should be in the list");
        
        // Clean up
        itemDao.delete(item1.getId());
        itemDao.delete(item2.getId());
    }

    @Test
    @DisplayName("Test update item")
    public void testUpdateItem() throws SQLException {
        // Create item
        Item createdItem = itemDao.create(testItem);
        assertNotNull(createdItem, "Created item should not be null");
        int itemId = createdItem.getId();
        assertTrue(itemId > 0, "Item ID should be greater than 0");
        
        // Update item
        createdItem.setName("Updated Name");
        createdItem.setDescription("Updated Description");
        createdItem.setQuantity(15);
        
        boolean updated = itemDao.update(createdItem);
        
        // Verify update was successful
        assertTrue(updated, "Update should be successful");
        
        // Read updated item
        Item updatedItem = itemDao.read(itemId);
        
        // Verify changes
        assertNotNull(updatedItem, "Updated item should not be null");
        assertEquals("Updated Name", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
        assertEquals(15, updatedItem.getQuantity());
        
        // Clean up
        itemDao.delete(itemId);
    }

    @Test
    @DisplayName("Test delete item")
    public void testDeleteItem() throws SQLException {
        // Create item
        Item createdItem = itemDao.create(testItem);
        assertNotNull(createdItem, "Created item should not be null");
        int itemId = createdItem.getId();
        assertTrue(itemId > 0, "Item ID should be greater than 0");
        
        // Delete item
        boolean deleted = itemDao.delete(itemId);
        
        // Verify deletion was successful
        assertTrue(deleted, "Delete should be successful");
        
        // Try to read deleted item
        Item readItem = itemDao.read(itemId);
        
        // Verify item was deleted
        assertNull(readItem, "Deleted item should be null");
    }
}