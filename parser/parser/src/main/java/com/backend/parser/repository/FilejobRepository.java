package com.backend.parser.repository;

import com.backend.parser.entities.Filejob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilejobRepository extends JpaRepository<Filejob, Long> {
}
