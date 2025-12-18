CREATE TABLE books (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       genre VARCHAR(100),
                       available BOOLEAN
);

CREATE TABLE members (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) UNIQUE NOT NULL,
                         phone_number VARCHAR(20)
);

CREATE TABLE loans (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       book_id BIGINT NOT NULL,
                       member_id BIGINT NOT NULL,
                       loan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       return_date TIMESTAMP,
                       FOREIGN KEY (book_id) REFERENCES books(id),
                       FOREIGN KEY (member_id) REFERENCES members(id)
);
