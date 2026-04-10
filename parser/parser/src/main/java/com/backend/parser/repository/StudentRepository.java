package com.backend.parser.repository;

import com.backend.parser.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    void deleteByJobId(String jobId);
}
