package com.backend.parser.repository;

import com.backend.parser.entities.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectsRepository extends JpaRepository<Subjects, Long> {
}
