package com.backend.parser.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "file_job")
public class Filejob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Long id;

    @Column(name="job_id", unique = true, nullable = false)
    private String jobId;

    @Column(name="total_files", nullable = false)
    private int totalFiles;

    @Column(name="files_processed", nullable = false)
    private int filesProcessed = 0;

    @Column(name="status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @OneToMany(mappedBy = "fileJob", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files;

}
