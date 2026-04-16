package com.backend.parser.repository;

import com.backend.parser.entities.Student;
import com.backend.parser.entities.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    void deleteByJobId(String jobId);

    List<Student> findByJobId(String jobId);

}
