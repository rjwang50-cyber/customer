CREATE DATABASE IF NOT EXISTS sg_customer_hub DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sg_customer_hub;

CREATE TABLE IF NOT EXISTS employee_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(120) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(120),
    birthday DATE,
    hobbies VARCHAR(255),
    notes TEXT,
    created_by BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_customer_birthday (birthday),
    INDEX idx_customer_email (email)
);

CREATE TABLE IF NOT EXISTS customer_social_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    platform VARCHAR(40) NOT NULL,
    account VARCHAR(120) NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_customer_platform (customer_id, platform)
);

CREATE TABLE IF NOT EXISTS blog_post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_employee_id BIGINT NOT NULL,
    title VARCHAR(160) NOT NULL,
    summary VARCHAR(255),
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    visibility VARCHAR(20) NOT NULL DEFAULT 'public',
    published_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_blog_status_published (status, published_at)
);

CREATE TABLE IF NOT EXISTS sg_holiday_calendar (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    holiday_date DATE NOT NULL,
    holiday_code VARCHAR(40) NOT NULL,
    holiday_name VARCHAR(120) NOT NULL,
    `year` INT NOT NULL,
    is_observed TINYINT NOT NULL DEFAULT 0,
    UNIQUE KEY uk_sg_holiday (holiday_date, holiday_code)
);

CREATE TABLE IF NOT EXISTS mail_send_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    send_type VARCHAR(30) NOT NULL,
    holiday_code VARCHAR(40) NOT NULL,
    target_date DATE NOT NULL,
    to_email VARCHAR(120) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL,
    error_msg VARCHAR(500),
    retry_count INT NOT NULL DEFAULT 0,
    sent_at DATETIME,
    created_at DATETIME NOT NULL,
    UNIQUE KEY uk_dedupe (customer_id, send_type, holiday_code, target_date)
);

INSERT INTO employee_user(username, password_hash, display_name, status, created_at, updated_at)
VALUES ('admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6M7qDpiRnbV0w8AV/QXp1S8Maqk7a', 'Admin', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Singapore holidays sample for 2026 (maintain yearly)
INSERT IGNORE INTO sg_holiday_calendar(holiday_date, holiday_code, holiday_name, `year`, is_observed) VALUES
('2026-01-01', 'NEW_YEAR', 'New Year''s Day', 2026, 0),
('2026-05-01', 'LABOUR_DAY', 'Labour Day', 2026, 0),
('2026-08-09', 'NATIONAL_DAY', 'National Day', 2026, 0),
('2026-08-10', 'NATIONAL_DAY_OBS', 'National Day (Observed)', 2026, 1),
('2026-12-25', 'CHRISTMAS', 'Christmas Day', 2026, 0);
