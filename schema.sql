-- Database schema for Job Skills Matching App

-- Create database
CREATE DATABASE IF NOT EXISTS job_matching;
USE job_matching;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('admin','employer','seeker') NOT NULL DEFAULT 'seeker',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create jobs table
CREATE TABLE IF NOT EXISTS jobs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    skills TEXT,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Create seekers table
CREATE TABLE IF NOT EXISTS seekers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE,
    skills TEXT,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- Insert sample data

-- Admin user (username: admin, password: admin123)
-- Password hash for "admin123": 99339fd259b7967086740093340700ab52b7a018e481f863a241d1e9f73e472f
INSERT INTO users (username, password_hash, role) VALUES 
('admin', '99339fd259b7967086740093340700ab52b7a018e481f863a241d1e9f73e472f', 'admin');

-- Sample jobs
INSERT INTO jobs (title, description, skills, created_by) VALUES 
('Java Developer', 'Develop Java applications', 'Java, Spring, Hibernate, MySQL', 1),
('Frontend Developer', 'Build user interfaces', 'JavaScript, HTML, CSS, React', 1),
('DevOps Engineer', 'Manage infrastructure', 'Docker, Kubernetes, AWS, Jenkins', 1);

-- Sample seekers
INSERT INTO seekers (name, email, skills, created_by) VALUES 
('Alice Johnson', 'alice@example.com', 'Java, Spring, MySQL, REST APIs', 1),
('Bob Smith', 'bob@example.com', 'JavaScript, React, CSS, Node.js', 1),
('Carol Davis', 'carol@example.com', 'Docker, AWS, Python, Terraform', 1);