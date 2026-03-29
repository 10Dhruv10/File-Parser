CREATE TABLE subjects(
         id BIGINT AUTO_INCREMENT PRIMARY KEY,
         subject_name VARCHAR(100) NOT NULL,
         grade VARCHAR(10) NOT NULL,
         student_id BIGINT NOT NULL,

         FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE
);