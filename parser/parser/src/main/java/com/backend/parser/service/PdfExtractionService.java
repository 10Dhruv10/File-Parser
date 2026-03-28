package com.backend.parser.service;

import com.backend.parser.entities.File;
import com.backend.parser.entities.Filejob;
import com.backend.parser.repository.FileRepository;
import com.backend.parser.repository.FilejobRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Service
public class PdfExtractionService {

    private final FilejobRepository filejobRepository;
    private final FileRepository fileRepository;

    public String extractText(byte[] bytes){
        try{
            PDDocument document = PDDocument.load(bytes);
            PDFTextStripper strip = new PDFTextStripper();
            return strip.getText(document);
        }
        catch(Exception e){
            throw new RuntimeException("Error extracting pdf", e);
        }
    }


    public List<String> extractTextFromAllFiles(String jobId){
        List<String> filesList = new ArrayList<>();
        Filejob filejob = filejobRepository.findByJobId(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        for (File file: filejob.getFiles()){
            String text = extractText(file.getFileData());
            filesList.add(text);
        }

        return filesList;
    }
}
