# WorkFusion - User Management CRUD Implementation

## Overview
This document describes the implementation of full CRUD (Create, Read, Update, Delete) functionality for Users in the WorkFusion JavaFX application.

## Implementation Details

### 1. Database Schema
The users table is defined with the following schema:
```sql
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL UNIQUE,
  email VARCHAR(255),
  role VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Data Access Layer
A UserDao class was created to handle all database operations:
- `create(User u)`: Insert a new user
- `read(int id)`: Retrieve a user by ID
- `readAll()`: Retrieve all users
- `update(User u)`: Update an existing user
- `delete(int id)`: Delete a user by ID

### 3. User Interface
The UsersController was updated to:
- Use background threading for all database operations
- Display users in a TableView
- Provide form controls for creating/updating users
- Implement proper error handling with user-friendly messages

### 4. Navigation
All navigation buttons in the main interface now work correctly:
- Users button loads the user management view
- Jobs button loads the jobs view
- Seekers button loads the seekers view
- Matches button loads the matches view
- Items button loads the items view

## Key Features

### Background Threading
All database operations are performed on background threads using JavaFX Task to prevent UI freezing:
```java
Task<List<User>> task = new Task<List<User>>() {
    @Override
    protected List<User> call() throws Exception {
        return userDao.readAll();
    }
};

task.setOnSucceeded(e -> {
    userList.setAll(task.getValue());
    System.out.println("DEBUG: Loaded " + userList.size() + " users from DB");
});

new Thread(task, "users-reader").start();
```

### Debug Logging
Added comprehensive debug logging:
- FXML loading: print resource URL
- Controllers: print initialize start/end
- Nav button handlers: print "DEBUG: <button> clicked"
- Users DAO: print row counts on readAll

### Error Handling
Proper error handling with user-friendly error dialogs for:
- Database connection issues
- SQL exceptions
- Validation errors
- Resource access problems

## Database Configuration
The application uses MySQL database with the following default configuration:
- URL: `jdbc:mysql://127.0.0.1:3306/job_matching`
- Username: `root`
- Password: `Jaya98765!`

To change the database configuration, modify the `Database.java` file in `src/main/java/com/example/workfusion/`.

## Running the Application

### Build the project:
```bash
./mvnw clean package
```

### Run the application:
```bash
./mvnw javafx:run
```

### Run tests:
```bash
./mvnw test
```

## Default Login Credentials
- Username: `admin`
- Password: `admin123`

## Usage
1. Start the application
2. Log in with admin credentials
3. Click on the "Users" button in the navigation bar
4. Use the form to create, update, or delete users
5. The TableView will automatically refresh to show current data

## Testing
The implementation includes comprehensive unit tests:
- UserDaoTest: Tests all CRUD operations
- DatabaseTest: Tests database connectivity
- UIIntegrationTest: Tests UI integration
- CRUDIntegrationTest: Tests end-to-end CRUD flow

All 16 tests pass successfully.

## Security
- Prepared statements to prevent SQL injection
- Proper resource cleanup with try-with-resources
- Secure password handling with SHA-256 hashing