package com.backend.parser.controller;

import com.backend.parser.entities.File;
import com.backend.parser.entities.Filejob;
import com.backend.parser.entities.Subjects;
import com.backend.parser.repository.FileRepository;
import com.backend.parser.repository.FilejobRepository;
import com.backend.parser.repository.StudentRepository;
import com.backend.parser.service.PdfExtractionService;
import com.backend.parser.service.ResultProcessingService;
import com.backend.parser.service.restClientService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;




@AllArgsConstructor
@RestController
@RequestMapping(("/backendApi"))
@CrossOrigin(origins = "http://localhost:4200")
public class uploadController {

    private final FilejobRepository filejobRepository;
    private final FileRepository fileRepository;
    private final PdfExtractionService pdfExtractionService;
    private final ResultProcessingService resultProcessingService;
    private final restClientService restClientService;
    private StudentRepository studentRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("filesFromAngular") List<MultipartFile> files){

            if (files.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded");
            }
            if (files.size() > 130){
                return ResponseEntity.badRequest().body("Too many files uploaded. Maximum allowed is 130.");
            }

            Filejob jobEntity = new Filejob();
            jobEntity.setJobId(UUID.randomUUID().toString());
            jobEntity.setStatus("PENDING");
            jobEntity.setTotalFiles(files.size());
            jobEntity.setFiles(new ArrayList<>());

            for (MultipartFile file : files) {
                if (file.getSize() > 200 * 1024 || !pdfExtractionService.ValidatePdf(file)) {
                    filejobRepository.delete(jobEntity);
                    return ResponseEntity.badRequest().body("Invalid file or File Size " + file.getOriginalFilename());
                }

                File fileEntity = new File();
                fileEntity.setFileName(file.getOriginalFilename());
                fileEntity.setFileType(file.getContentType());
                fileEntity.setFileSize(file.getSize());
                try {
                    fileEntity.setFileData(file.getBytes());
                }
                catch(IOException e){
                    return ResponseEntity.status(500).body("Error saving file: " + e.getMessage());
                }
                fileEntity.setStatus("UPLOADED");
                fileEntity.setFileJob(jobEntity);
                jobEntity.getFiles().add(fileEntity);
            }

            filejobRepository.save(jobEntity);    // Save job and files in one transaction due to cascade


            byte[] response = resultProcessingService.processResults(jobEntity.getJobId());
            initiateSendingStudentData(jobEntity.getJobId());

        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=results.xlsx")
                .body(response);

    }

    public void initiateSendingStudentData(String jobId){
        Map<String, Object> studentData = new HashMap<>();
        String name = studentRepository.findNameByJobId(jobId);
        String prn = studentRepository.findPrnByJobId(jobId);
        Double sgpa = studentRepository.findSgpaByJobId(jobId);
        List<Subjects> subjects = studentRepository.findSubjectsByJobId(jobId);

        studentData.put("name", name);
        studentData.put("prn", prn);
        studentData.put("sgpa", sgpa);
        studentData.put("subjects", subjects);

        restClientService.postStudentData(studentData);
    }

}