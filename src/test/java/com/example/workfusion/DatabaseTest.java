package com.example.workfusion;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    private Job testJob;
    private Seeker testSeeker;

    @BeforeEach
    void setUp() {
        // Ensure the database is initialized
        Database.runMigrations();
    }

    @AfterEach
    void tearDown() throws SQLException {
        // Clean up any test data
        if (testJob != null) {
            try {
                Database.deleteJob(testJob.getId());
            } catch (SQLException e) {
                // Ignore if job doesn't exist
            }
        }
        if (testSeeker != null) {
            try {
                Database.deleteSeeker(testSeeker.getId());
            } catch (SQLException e) {
                // Ignore if seeker doesn't exist
            }
        }
    }

    @Test
    void testJobCRUDOperations() throws SQLException {
        // Create
        testJob = new Job("Test Job", "Test Description", "Java, SQL", 1);
        Database.insertJob(testJob);
        assertNotEquals(0, testJob.getId(), "Job ID should be set after insertion");

        // Read
        List<Job> jobs = Database.getAllJobs();
        assertFalse(jobs.isEmpty(), "Should have at least one job");
        Job foundJob = jobs.stream()
                .filter(j -> j.getId() == testJob.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(foundJob, "Should find the inserted job");
        assertEquals("Test Job", foundJob.getTitle());
        assertEquals("Test Description", foundJob.getDescription());
        assertEquals("Java, SQL", foundJob.getSkills());

        // Update
        testJob.setTitle("Updated Test Job");
        testJob.setDescription("Updated Description");
        testJob.setSkills("Java, SQL, HTML");
        Database.updateJob(testJob);

        List<Job> updatedJobs = Database.getAllJobs();
        Job updatedJob = updatedJobs.stream()
                .filter(j -> j.getId() == testJob.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(updatedJob, "Should find the updated job");
        assertEquals("Updated Test Job", updatedJob.getTitle());
        assertEquals("Updated Description", updatedJob.getDescription());
        assertEquals("Java, SQL, HTML", updatedJob.getSkills());

        // Delete
        int jobId = testJob.getId();
        Database.deleteJob(jobId);

        List<Job> finalJobs = Database.getAllJobs();
        Job deletedJob = finalJobs.stream()
                .filter(j -> j.getId() == jobId)
                .findFirst()
                .orElse(null);
        assertNull(deletedJob, "Should not find the deleted job");
        testJob = null; // Mark as deleted
    }

    @Test
    void testSeekerCRUDOperations() throws SQLException {
        // Create
        testSeeker = new Seeker("Test Seeker", "test@example.com", "Java, SQL", 1);
        Database.insertSeeker(testSeeker);
        assertNotEquals(0, testSeeker.getId(), "Seeker ID should be set after insertion");

        // Read
        List<Seeker> seekers = Database.getAllSeekers();
        assertFalse(seekers.isEmpty(), "Should have at least one seeker");
        Seeker foundSeeker = seekers.stream()
                .filter(s -> s.getId() == testSeeker.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(foundSeeker, "Should find the inserted seeker");
        assertEquals("Test Seeker", foundSeeker.getName());
        assertEquals("test@example.com", foundSeeker.getEmail());
        assertEquals("Java, SQL", foundSeeker.getSkills());

        // Update
        testSeeker.setName("Updated Test Seeker");
        testSeeker.setEmail("updated@example.com");
        testSeeker.setSkills("Java, SQL, HTML");
        Database.updateSeeker(testSeeker);

        List<Seeker> updatedSeekers = Database.getAllSeekers();
        Seeker updatedSeeker = updatedSeekers.stream()
                .filter(s -> s.getId() == testSeeker.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(updatedSeeker, "Should find the updated seeker");
        assertEquals("Updated Test Seeker", updatedSeeker.getName());
        assertEquals("updated@example.com", updatedSeeker.getEmail());
        assertEquals("Java, SQL, HTML", updatedSeeker.getSkills());

        // Delete
        int seekerId = testSeeker.getId();
        Database.deleteSeeker(seekerId);

        List<Seeker> finalSeekers = Database.getAllSeekers();
        Seeker deletedSeeker = finalSeekers.stream()
                .filter(s -> s.getId() == seekerId)
                .findFirst()
                .orElse(null);
        assertNull(deletedSeeker, "Should not find the deleted seeker");
        testSeeker = null; // Mark as deleted
    }
}