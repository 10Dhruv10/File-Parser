package com.backend.parser.repository;

import com.backend.parser.entities.File;
import com.backend.parser.entities.Filejob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FilejobRepository extends JpaRepository<Filejob, Long> {
    Optional<Filejob> findByJobId(String jobId);

    List<Filejob> findByCreatedAtBefore(LocalDateTime cutoffTime);
}
