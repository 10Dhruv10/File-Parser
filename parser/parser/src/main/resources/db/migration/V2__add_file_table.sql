CREATE TABLE file (
      id BIGINT PRIMARY KEY AUTO_INCREMENT,
      file_name VARCHAR(255) NOT NULL,
      file_type VARCHAR(100),
      file_size BIGINT NOT NULL,
      file_data MEDIUMBLOB NOT NULL,
      status VARCHAR(20) NOT NULL,
      job_id BIGINT NOT NULL,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      FOREIGN KEY (job_id) REFERENCES file_job(id) ON DELETE CASCADE
);