package com.backend.parser.service;

import com.backend.parser.entities.Filejob;
import com.backend.parser.repository.FilejobRepository;
import com.backend.parser.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class ScheduledDeletionService {
        FilejobRepository fileJobRepository;
        StudentRepository studentRepository;

        /*
         This method runs automatically every 5 minutes & deletes Filejob that are older than 30 minutes.
         */
        @Scheduled(cron = "0 */1 * * * *")
        @Transactional //If any deletion fails, rollback entire transaction to maintain data integrity, important.
        public void deleteOldFiles() {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30);
            System.out.println(cutoffTime);
            System.out.println(fileJobRepository.findByCreatedAtBefore(cutoffTime));

            for (Filejob job : fileJobRepository.findByCreatedAtBefore(cutoffTime)){
                System.out.println("Deleting job with ID: " + job.getJobId() + " created at: " + job.getCreatedAt());
                studentRepository.deleteByJobId(job.getJobId());
                fileJobRepository.delete(job);
            }
        }
}
