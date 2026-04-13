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

         If any deletion fails, rollback the entire transaction to maintain data integrity, Important.
         */
        @Scheduled(cron = "0 */5 * * * *")
        @Transactional
        public void deleteOldFiles() {
            LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30);

            for (Filejob job : fileJobRepository.findByCreatedAtBefore(cutoffTime)){
                studentRepository.deleteByJobId(job.getJobId());
                fileJobRepository.delete(job);
            }
        }
}
