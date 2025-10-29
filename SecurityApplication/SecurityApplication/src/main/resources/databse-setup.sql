-- ============================================
-- SPRING SECURITY DATABASE SETUP
-- ============================================
-- This script creates the database and tables manually
-- NOTE: Hibernate can create tables automatically, but this is useful
-- if you want to create the database structure manually

-- ============================================
-- CREATE DATABASE
-- ============================================
-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS spring_security_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE spring_security_db;

-- ============================================
-- DROP EXISTING TABLES (if recreating)
-- ============================================
-- Uncomment these lines if you want to recreate tables
-- DROP TABLE IF EXISTS users;

-- ============================================
-- CREATE USERS TABLE
-- ============================================
-- This table stores user credentials and roles
CREATE TABLE IF NOT EXISTS users (
    -- Primary key: Auto-incrementing ID
    id BIGINT NOT NULL AUTO_INCREMENT,

    -- Username: Used for login, must be unique
    username VARCHAR(50) NOT NULL UNIQUE,

    -- Password: BCrypt encrypted password
    -- BCrypt hashes are 60 characters long
    -- Format: $2a$10$[22 char salt][31 char hash]
    password VARCHAR(255) NOT NULL,

    -- Email: User's email address
    email VARCHAR(100) NOT NULL,

    -- Role: User's role (ADMIN or USER)
    -- Used for authorization (determining permissions)
    role VARCHAR(20) NOT NULL,

    -- Enabled: Account status
    -- If false, user cannot login even with correct credentials
    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    -- Primary key constraint
    PRIMARY KEY (id),

    -- Unique constraint on username
    CONSTRAINT uk_username UNIQUE (username),

    -- Unique constraint on email
    CONSTRAINT uk_email UNIQUE (email),

    -- Check constraint for role (only ADMIN or USER allowed)
    CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'USER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- CREATE INDEXES FOR PERFORMANCE
-- ============================================
-- Index on username for faster login queries
CREATE INDEX idx_username ON users(username);

-- Index on email for faster email lookups
CREATE INDEX idx_email ON users(email);

-- Index on role for faster authorization queries
CREATE INDEX idx_role ON users(role);

-- ============================================
-- INSERT DEFAULT USERS
-- ============================================
-- Insert admin user
-- Password: admin123 (BCrypt encrypted)
-- Note: The hash below is for "admin123"
INSERT INTO users (username, password, email, role, enabled) VALUES
(
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin@example.com',
    'ADMIN',
    TRUE
);

-- Insert regular user
-- Password: user123 (BCrypt encrypted)
-- Note: The hash below is for "user123"
INSERT INTO users (username, password, email, role, enabled) VALUES
(
    'user',
    '$2a$10$DOwnh2KsI5hf6RWZBz0GiOd4qyqY1Y9cNMdZJ6XHYKKKFMcBvVEzO',
    'user@example.com',
    'USER',
    TRUE
);

-- ============================================
-- VERIFY DATA
-- ============================================
-- Query to verify users were created
SELECT
    id,
    username,
    email,
    role,
    enabled,
    CONCAT('$2a$10$...', SUBSTRING(password, -10)) AS password_preview
FROM users;

-- ============================================
-- ADDITIONAL QUERIES (FOR TESTING)
-- ============================================

-- Count total users
SELECT COUNT(*) AS total_users FROM users;

-- Count users by role
SELECT role, COUNT(*) AS count
FROM users
GROUP BY role;

-- Find all admins
SELECT username, email, enabled
FROM users
WHERE role = 'ADMIN';

-- Find all regular users
SELECT username, email, enabled
FROM users
WHERE role = 'USER';

-- Find enabled users only
SELECT username, email, role
FROM users
WHERE enabled = TRUE;

-- ============================================
-- SECURITY NOTES
-- ============================================
/*
IMPORTANT SECURITY CONSIDERATIONS:

1. PASSWORD ENCRYPTION:
   - Never store plain text passwords
   - BCrypt is used for password hashing
   - Each password has a unique salt
   - Same password produces different hash each time

2. PASSWORD FORMAT:
   - BCrypt format: $2a$10$[salt][hash]
   - $2a = BCrypt algorithm identifier
   - $10 = Cost factor (2^10 = 1024 iterations)
   - Next 22 chars = Salt
   - Last 31 chars = Password hash

3. GENERATING NEW PASSWORD HASHES:
   - Never manually create BCrypt hashes
   - Always use BCryptPasswordEncoder in Java:

     @Autowired
     private PasswordEncoder passwordEncoder;

     String encrypted = passwordEncoder.encode("plainPassword");

4. USERNAME SECURITY:
   - Usernames are case-sensitive
   - Must be unique (database constraint)
   - Cannot be changed easily (affects authentication)

5. EMAIL SECURITY:
   - Should be validated
   - Should be unique
   - Can be used for password reset

6. ROLE SECURITY:
   - Only ADMIN and USER roles allowed (check constraint)
   - Roles determine authorization (permissions)
   - Role changes require re-login to take effect

7. ACCOUNT STATUS:
   - 'enabled' field controls account activation
   - Disabled accounts cannot login
   - Useful for account suspension

8. DATABASE SECURITY:
   - Use strong MySQL root password
   - Create dedicated database user for application:

     CREATE USER 'springapp'@'localhost' IDENTIFIED BY 'strong_password';
     GRANT ALL PRIVILEGES ON spring_security_db.* TO 'springapp'@'localhost';
     FLUSH PRIVILEGES;

9. PRODUCTION CONSIDERATIONS:
   - Never commit database credentials to version control
   - Use environment variables for sensitive data
   - Enable MySQL SSL connections
   - Regularly backup database
   - Monitor failed login attempts
   - Implement account lockout after failed attempts
   - Log all authentication events
*/

-- ============================================
-- END OF SCRIPT
-- ============================================