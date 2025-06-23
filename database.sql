-- Create the database
CREATE DATABASE IF NOT EXISTS `fire_db` DEFAULT CHARACTER
SET
    utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `fire_db`;

-- Create the user table
CREATE TABLE
    IF NOT EXISTS `users` (
        `id` INT AUTO_INCREMENT PRIMARY KEY,
        `username` VARCHAR(50) NOT NULL UNIQUE,
        `password` VARCHAR(255) NOT NULL,
        `role` VARCHAR(20) NOT NULL DEFAULT 'user',
        `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Insert a default admin user (password: admin)
-- In a real system, passwords should be hashed.
INSERT INTO
    `users` (`username`, `password`, `role`)
VALUES
    ('admin', 'admin', 'admin');