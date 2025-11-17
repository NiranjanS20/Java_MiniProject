package com.example.workfusion;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UIIntegrationTest {

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
    void testUIUpdatesAfterAddingJob() throws SQLException {
        // Get initial count of jobs
        List<Job> initialJobs = Database.getAllJobs();
        int initialCount = initialJobs.size();
        
        // Simulate UI adding a job (this is what the JobsController does)
        testJob = new Job("UI Test Job", "Test Description", "Java, Testing", 1);
        Database.insertJob(testJob);
        
        // Verify job was added to database
        assertNotEquals(0, testJob.getId(), "Job ID should be set after insertion");
        
        // Get updated count of jobs
        List<Job> updatedJobs = Database.getAllJobs();
        assertEquals(initialCount + 1, updatedJobs.size(), "Job count should increase by 1");
        
        // Verify the job exists in the list
        Job foundJob = updatedJobs.stream()
                .filter(j -> j.getId() == testJob.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(foundJob, "Should find the inserted job");
        assertEquals("UI Test Job", foundJob.getTitle());
    }

    @Test
    void testUIUpdatesAfterUpdatingJob() throws SQLException {
        // Create a job first
        testJob = new Job("Original Job", "Original Description", "Java", 1);
        Database.insertJob(testJob);
        
        // Simulate UI updating the job
        testJob.setTitle("Updated Job");
        testJob.setDescription("Updated Description");
        testJob.setSkills("Java, SQL");
        Database.updateJob(testJob);
        
        // Verify the job was updated in the database
        List<Job> jobs = Database.getAllJobs();
        Job updatedJob = jobs.stream()
                .filter(j -> j.getId() == testJob.getId())
                .findFirst()
                .orElse(null);
        assertNotNull(updatedJob, "Should find the updated job");
        assertEquals("Updated Job", updatedJob.getTitle());
        assertEquals("Updated Description", updatedJob.getDescription());
        assertEquals("Java, SQL", updatedJob.getSkills());
    }

    @Test
    void testUIUpdatesAfterDeletingJob() throws SQLException {
        // Create a job first
        testJob = new Job("Job to Delete", "Description", "Java", 1);
        Database.insertJob(testJob);
        
        // Get initial count
        int initialCount = Database.getAllJobs().size();
        
        // Simulate UI deleting the job
        int jobId = testJob.getId();
        Database.deleteJob(jobId);
        testJob = null; // Mark as deleted
        
        // Verify the job was deleted from the database
        List<Job> jobs = Database.getAllJobs();
        assertEquals(initialCount - 1, jobs.size(), "Job count should decrease by 1");
        
        Job deletedJob = jobs.stream()
                .filter(j -> j.getId() == jobId)
                .findFirst()
                .orElse(null);
        assertNull(deletedJob, "Should not find the deleted job");
    }
}