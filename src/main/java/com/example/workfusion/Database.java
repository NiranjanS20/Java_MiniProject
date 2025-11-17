package com.example.workfusion;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/job_matching";
    private static final String USER = "root";
    private static final String PASSWORD = "Jaya98765!";

    // Get database connection
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    // Close database resources
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }

    // Run database migrations
    public static void runMigrations() {
        createUsersTable();
        createJobsTable();
        createSeekersTable();
        createItemsTable();
        
        // Insert admin user if no users exist
        if (isUsersEmpty()) {
            insertSampleData();
        }
        
        // Insert sample items if items table is empty
        if (isItemsEmpty()) {
            insertSampleItems();
        }
    }

    // Check if users table is empty
    public static boolean isUsersEmpty() {
        String sql = "SELECT COUNT(*) AS count FROM users";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("count") == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if users table is empty: " + e.getMessage());
        }
        
        return true;
    }

    // Check if items table is empty
    public static boolean isItemsEmpty() {
        String sql = "SELECT COUNT(*) AS count FROM items";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("count") == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if items table is empty: " + e.getMessage());
        }
        
        return true;
    }

    // Create users table
    private static void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(100) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(255) NOT NULL, " +
                "role ENUM('admin','employer','seeker') NOT NULL DEFAULT 'seeker', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
                
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating users table: " + e.getMessage());
        }
    }

    // Create jobs table
    private static void createJobsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS jobs (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(150) NOT NULL, " +
                "description TEXT, " +
                "skills TEXT, " +
                "created_by INT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (created_by) REFERENCES users(id)" +
                ")";
                
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating jobs table: " + e.getMessage());
        }
    }

    // Create seekers table
    private static void createSeekersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS seekers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(150) NOT NULL, " +
                "email VARCHAR(150) UNIQUE, " +
                "skills TEXT, " +
                "created_by INT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (created_by) REFERENCES users(id)" +
                ")";
                
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating seekers table: " + e.getMessage());
        }
    }

    // Create items table
    private static void createItemsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS items (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "description TEXT, " +
                "quantity INT DEFAULT 0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
                
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error creating items table: " + e.getMessage());
        }
    }

    // Insert sample data
    private static void insertSampleData() {
        try {
            // Insert admin user (password: admin123)
            String adminPasswordHash = hashPassword("admin123");
            insertUser(new User("admin", adminPasswordHash, "admin"));
            
            // Insert sample jobs
            insertJob(new Job("Java Developer", "Develop Java applications", "Java, Spring, Hibernate, MySQL", 1));
            insertJob(new Job("Frontend Developer", "Build user interfaces", "JavaScript, HTML, CSS, React", 1));
            insertJob(new Job("DevOps Engineer", "Manage infrastructure", "Docker, Kubernetes, AWS, Jenkins", 1));
            
            // Insert sample seekers
            insertSeeker(new Seeker("Alice Johnson", "alice@example.com", "Java, Spring, MySQL, REST APIs", 1));
            insertSeeker(new Seeker("Bob Smith", "bob@example.com", "JavaScript, React, CSS, Node.js", 1));
            insertSeeker(new Seeker("Carol Davis", "carol@example.com", "Docker, AWS, Python, Terraform", 1));
            
        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
        }
    }

    // Insert sample items
    private static void insertSampleItems() {
        try {
            // Insert sample items
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO items (name, description, quantity) VALUES (?, ?, ?)")) {
                
                // Item 1
                pstmt.setString(1, "Seed 1");
                pstmt.setString(2, "First seed item for testing");
                pstmt.setInt(3, 1);
                pstmt.executeUpdate();
                
                // Item 2
                pstmt.setString(1, "Seed 2");
                pstmt.setString(2, "Second seed item for testing");
                pstmt.setInt(3, 2);
                pstmt.executeUpdate();
                
                // Item 3
                pstmt.setString(1, "Seed 3");
                pstmt.setString(2, "Third seed item for testing");
                pstmt.setInt(3, 3);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error inserting sample items: " + e.getMessage());
        }
    }

    // Hash password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    // Verify password
    public static boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }

    // User operations
    public static void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password_hash, role) VALUES(?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.executeUpdate();
            
            // Get generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        }
    }

    public static User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password_hash, role, created_at FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setRole(rs.getString("role"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        }
        
        return null;
    }

    public static List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, password_hash, role, created_at FROM users ORDER BY username";
        
        try (Connection conn = getConnection();
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
        }
        
        return users;
    }

    public static void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password_hash = ?, role = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getRole());
            pstmt.setInt(4, user.getId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    // Job operations
    public static void insertJob(Job job) throws SQLException {
        String sql = "INSERT INTO jobs(title, description, skills, created_by) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, job.getTitle());
            pstmt.setString(2, job.getDescription());
            pstmt.setString(3, job.getSkills());
            pstmt.setInt(4, job.getCreatedBy());
            pstmt.executeUpdate();
            
            // Get generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    job.setId(rs.getInt(1));
                }
            }
        }
    }

    public static List<Job> getAllJobs() throws SQLException {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT id, title, description, skills, created_by, created_at FROM jobs ORDER BY title";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Job job = new Job();
                job.setId(rs.getInt("id"));
                job.setTitle(rs.getString("title"));
                job.setDescription(rs.getString("description"));
                job.setSkills(rs.getString("skills"));
                job.setCreatedBy(rs.getInt("created_by"));
                job.setCreatedAt(rs.getTimestamp("created_at"));
                jobs.add(job);
            }
        }
        
        return jobs;
    }

    public static void updateJob(Job job) throws SQLException {
        String sql = "UPDATE jobs SET title = ?, description = ?, skills = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, job.getTitle());
            pstmt.setString(2, job.getDescription());
            pstmt.setString(3, job.getSkills());
            pstmt.setInt(4, job.getId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteJob(int jobId) throws SQLException {
        String sql = "DELETE FROM jobs WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, jobId);
            pstmt.executeUpdate();
        }
    }

    // Seeker operations
    public static void insertSeeker(Seeker seeker) throws SQLException {
        String sql = "INSERT INTO seekers(name, email, skills, created_by) VALUES(?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, seeker.getName());
            pstmt.setString(2, seeker.getEmail());
            pstmt.setString(3, seeker.getSkills());
            pstmt.setInt(4, seeker.getCreatedBy());
            pstmt.executeUpdate();
            
            // Get generated ID
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    seeker.setId(rs.getInt(1));
                }
            }
        }
    }

    public static List<Seeker> getAllSeekers() throws SQLException {
        List<Seeker> seekers = new ArrayList<>();
        String sql = "SELECT id, name, email, skills, created_by, created_at FROM seekers ORDER BY name";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Seeker seeker = new Seeker();
                seeker.setId(rs.getInt("id"));
                seeker.setName(rs.getString("name"));
                seeker.setEmail(rs.getString("email"));
                seeker.setSkills(rs.getString("skills"));
                seeker.setCreatedBy(rs.getInt("created_by"));
                seeker.setCreatedAt(rs.getTimestamp("created_at"));
                seekers.add(seeker);
            }
        }
        
        return seekers;
    }

    public static void updateSeeker(Seeker seeker) throws SQLException {
        String sql = "UPDATE seekers SET name = ?, email = ?, skills = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, seeker.getName());
            pstmt.setString(2, seeker.getEmail());
            pstmt.setString(3, seeker.getSkills());
            pstmt.setInt(4, seeker.getId());
            pstmt.executeUpdate();
        }
    }

    public static void deleteSeeker(int seekerId) throws SQLException {
        String sql = "DELETE FROM seekers WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seekerId);
            pstmt.executeUpdate();
        }
    }
}