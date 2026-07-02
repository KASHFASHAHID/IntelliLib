-- ===========================================
-- Brainware Smart Library Database
-- Author: Kashfa Shahid
-- ===========================================

CREATE DATABASE IF NOT EXISTS brainware_smart_library;

USE brainware_smart_library;

CREATE TABLE users (
    user_id VARCHAR(30) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'LIBRARIAN', 'STUDENT', 'TEACHER') NOT NULL,
    university VARCHAR(100) NOT NULL,
    department VARCHAR(100),
    account_status ENUM('ACTIVE', 'BLOCKED', 'SUSPENDED') DEFAULT 'ACTIVE',
    must_change_password BOOLEAN DEFAULT TRUE,
    profile_photo_path VARCHAR(255),
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE membership_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    brainware_id VARCHAR(50) NOT NULL,
    university VARCHAR(100) NOT NULL,
    role_requested ENUM('STUDENT', 'TEACHER') NOT NULL,
    course_or_designation VARCHAR(100),
    department VARCHAR(100) NOT NULL,
    semester INT,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    photo_path VARCHAR(255),
    id_card_path VARCHAR(255),
    reason TEXT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    reviewed_by VARCHAR(30),
    reviewed_at TIMESTAMP NULL,
    remarks TEXT,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reviewed_by) REFERENCES users(user_id)
);

CREATE TABLE library_cards (
    card_number VARCHAR(30) PRIMARY KEY,
    user_id VARCHAR(30) UNIQUE NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status ENUM('ACTIVE', 'BLOCKED', 'EXPIRED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE authors (
    author_id INT AUTO_INCREMENT PRIMARY KEY,
    author_name VARCHAR(100) NOT NULL
);

CREATE TABLE books (
    isbn VARCHAR(30) PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    category_id INT,
    publisher VARCHAR(100),
    edition VARCHAR(50),
    language VARCHAR(50),
    publication_year INT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE book_authors (
    isbn VARCHAR(30),
    author_id INT,
    PRIMARY KEY (isbn, author_id),
    FOREIGN KEY (isbn) REFERENCES books(isbn),
    FOREIGN KEY (author_id) REFERENCES authors(author_id)
);

CREATE TABLE book_copies (
    copy_number VARCHAR(30) PRIMARY KEY,
    isbn VARCHAR(30) NOT NULL,
    barcode VARCHAR(50) UNIQUE NOT NULL,
    shelf_location VARCHAR(50) NOT NULL,
    status ENUM(
        'AVAILABLE',
        'ISSUED',
        'RESERVED',
        'LOST',
        'DAMAGED',
        'MAINTENANCE'
    ) DEFAULT 'AVAILABLE',
    purchase_date DATE,
    price DECIMAL(10,2),
    condition_note VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

CREATE TABLE book_loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    copy_number VARCHAR(30) NOT NULL,
    issued_by VARCHAR(30),
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    status ENUM('ISSUED', 'RETURNED', 'OVERDUE') DEFAULT 'ISSUED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (copy_number) REFERENCES book_copies(copy_number),
    FOREIGN KEY (issued_by) REFERENCES users(user_id)
);

CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    isbn VARCHAR(30) NOT NULL,
    status ENUM('WAITING', 'COMPLETED', 'CANCELLED') DEFAULT 'WAITING',
    reservation_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (isbn) REFERENCES books(isbn)
);

CREATE TABLE fines (
    fine_id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    user_id VARCHAR(30) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'PAID') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP NULL,
    FOREIGN KEY (loan_id) REFERENCES book_loans(loan_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(30) NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE activity_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(30),
    action VARCHAR(150) NOT NULL,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);