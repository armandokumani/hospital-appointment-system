-- ====================================================================
-- Hospital Appointment Management System - Database Initialization
-- ====================================================================

CREATE DATABASE IF NOT EXISTS `login_schema` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `login_schema`;

-- --------------------------------------------------------------------
-- Table structure: users
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `appointments`;
DROP TABLE IF EXISTS `doctors`;
DROP TABLE IF EXISTS `patients`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `surname` VARCHAR(50) NOT NULL,
  `age` INT NOT NULL,
  `type` ENUM('ADMIN','DOCTOR','PATIENT') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------------------
-- Table structure: doctors
-- --------------------------------------------------------------------
CREATE TABLE `doctors` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `specialty` VARCHAR(100) NOT NULL,
  `available_time` VARCHAR(100) DEFAULT NULL,
  `appointment_duration` INT DEFAULT 30,
  PRIMARY KEY (`id`),
  KEY `fk_doctors_user` (`user_id`),
  CONSTRAINT `fk_doctors_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------------------
-- Table structure: patients
-- --------------------------------------------------------------------
CREATE TABLE `patients` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `amka` VARCHAR(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_amka` (`amka`),
  KEY `fk_patients_user` (`user_id`),
  CONSTRAINT `fk_patients_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------------------
-- Table structure: appointments
-- --------------------------------------------------------------------
CREATE TABLE `appointments` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `patient_id` INT NOT NULL,
  `doctor_id` INT NOT NULL,
  `appointment_date` DATE NOT NULL,
  `appointment_time` TIME NOT NULL,
  `status` ENUM('SCHEDULED','CANCELLED','COMPLETED') DEFAULT 'SCHEDULED',
  PRIMARY KEY (`id`),
  KEY `fk_appointments_patient` (`patient_id`),
  KEY `fk_appointments_doctor` (`doctor_id`),
  CONSTRAINT `fk_appointments_patient` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_appointments_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `doctors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------------------
-- Demo Initial Data (Passwords hashed with BCrypt)
-- Default pass for demo accounts: "password123"
-- --------------------------------------------------------------------
INSERT INTO `users` (`id`, `username`, `password`, `name`, `surname`, `age`, `type`) VALUES
(1, 'admin', '$2a$12$e8wScmhB2uUo3b2LwB9F7.L8m8n9q.8FvO3k9A2g5L6t7U8v9w0x1', 'System', 'Admin', 40, 'ADMIN'),
(2, 'doc_john', '$2a$12$e8wScmhB2uUo3b2LwB9F7.L8m8n9q.8FvO3k9A2g5L6t7U8v9w0x1', 'John', 'Doe', 45, 'DOCTOR'),
(3, 'doc_jane', '$2a$12$e8wScmhB2uUo3b2LwB9F7.L8m8n9q.8FvO3k9A2g5L6t7U8v9w0x1', 'Jane', 'Smith', 38, 'DOCTOR'),
(4, 'patient_mike', '$2a$12$e8wScmhB2uUo3b2LwB9F7.L8m8n9q.8FvO3k9A2g5L6t7U8v9w0x1', 'Mike', 'Johnson', 30, 'PATIENT'),
(5, 'patient_sarah', '$2a$12$e8wScmhB2uUo3b2LwB9F7.L8m8n9q.8FvO3k9A2g5L6t7U8v9w0x1', 'Sarah', 'Williams', 25, 'PATIENT');

INSERT INTO `doctors` (`id`, `user_id`, `specialty`, `available_time`, `appointment_duration`) VALUES
(1, 2, 'Cardiology', '09:00-14:00', 30),
(2, 3, 'Pediatrics', '10:00-16:00', 30);

INSERT INTO `patients` (`id`, `user_id`, `amka`) VALUES
(1, 4, '12345678901'),
(2, 5, '98765432109');
