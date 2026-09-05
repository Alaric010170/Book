CREATE TABLE IF NOT EXISTS t_admin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(50),
    avatar VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS t_reader (
    id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL,
    level VARCHAR(20) NOT NULL,
    borrows INT NOT NULL DEFAULT 0,
    avatar VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS t_book (
    id VARCHAR(30) PRIMARY KEY,
    cover VARCHAR(500),
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS t_borrowing (
    id VARCHAR(30) PRIMARY KEY,
    reader_id VARCHAR(20) NOT NULL,
    reader_name VARCHAR(100) NOT NULL,
    reader_initials VARCHAR(10) NOT NULL,
    book_title VARCHAR(200) NOT NULL,
    borrow_date VARCHAR(50) NOT NULL,
    due_date VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    overdue_days INT
);

CREATE TABLE IF NOT EXISTS t_notification (
    id VARCHAR(30) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    time VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0
);
