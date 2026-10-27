
USE todo_list_db;

CREATE TABLE tasks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    task_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending'
);

INSERT INTO tasks (task_name, status) VALUES
('Complete Java assignment', 'Pending'),
('Study for exam', 'Pending'),
('Submit project report', 'Completed'),
('Revise SQL', 'Pending');

SELECT * FROM tasks;