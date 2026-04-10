package com.backend.parser.service;

import com.backend.parser.entities.File;
import com.backend.parser.entities.Filejob;
import com.backend.parser.entities.Student;
import com.backend.parser.entities.Subjects;
import com.backend.parser.repository.FileRepository;
import com.backend.parser.repository.FilejobRepository;
import com.backend.parser.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Service
public class PdfExtractionService {

    private final FilejobRepository filejobRepository;

    /*
    Instead of loading entire file into memory Load the PDF as input streams ,
    within try() load the streams so PDFs will be closed after use automatically.
     */
    public boolean ValidatePdf(MultipartFile file){

        try(InputStream inputStream = file.getInputStream();
            PDDocument loadedDocument = PDDocument.load(inputStream)) {

            if (loadedDocument.getNumberOfPages() == 0 || loadedDocument.getNumberOfPages() > 2){
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractText(byte[] bytes) {
        try {
            PDDocument document = PDDocument.load(bytes);
            PDFTextStripper strip = new PDFTextStripper();
            return strip.getText(document);
        } catch (Exception e) {
            throw new RuntimeException("Error extracting pdf", e);
        }
    }


    public List<String> extractTextFromAllFiles(String jobId) {
        List<String> filesList = new ArrayList<>();
        Filejob filejob = filejobRepository.findByJobId(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        for (File file : filejob.getFiles()) {
            String text = extractText(file.getFileData());
            filesList.add(text);
        }
        return filesList;
    }
}
