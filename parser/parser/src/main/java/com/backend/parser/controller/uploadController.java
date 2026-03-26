package com.backend.parser.controller;

import com.backend.parser.entities.File;
import com.backend.parser.entities.Filejob;
import com.backend.parser.repository.FileRepository;
import com.backend.parser.repository.FilejobRepository;
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

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("filesFromAngular") List<MultipartFile> files
            ){

            if (files.isEmpty()) {
                return ResponseEntity.badRequest().body("No file uploaded");
            }

            Filejob jobEntity = new Filejob();
            jobEntity.setJobId(UUID.randomUUID().toString());
            jobEntity.setStatus("PENDING");
            jobEntity.setTotalFiles(files.size());
            jobEntity.setFiles(new ArrayList<>());

            for (MultipartFile file : files) {
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


            Map<String, String> response = new HashMap<>();
            response.put("fileName", files.get(0).getOriginalFilename()); //bruh change this later

            return ResponseEntity.ok(response);

    }
}