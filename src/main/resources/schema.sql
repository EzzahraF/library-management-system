CREATE TABLE books (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(100) NOT NULL,
                       author VARCHAR(100) NOT NULL,
                       genre VARCHAR(50),
                       available BOOLEAN
);
