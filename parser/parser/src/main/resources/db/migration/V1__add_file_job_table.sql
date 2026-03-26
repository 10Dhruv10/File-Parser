CREATE TABLE file_job(
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     job_id          VARCHAR(36) UNIQUE NOT NULL,
     total_files     INT                NOT NULL,
     files_processed INT                NOT NULL DEFAULT 0,
     status         VARCHAR(20)        NOT NULL,
     created_at      TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP,
     completed_at    TIMESTAMP          NULL,
     error_message   TEXT NULL
);
