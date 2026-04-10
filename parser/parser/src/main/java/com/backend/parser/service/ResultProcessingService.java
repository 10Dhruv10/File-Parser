package com.backend.parser.service;

import com.backend.parser.entities.File;
import com.backend.parser.entities.Student;
import com.backend.parser.entities.Subjects;
import com.backend.parser.repository.FilejobRepository;
import com.backend.parser.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Service
public class ResultProcessingService {
    private final FilejobRepository filejobRepository;
    private final StudentRepository studentRepository;

    /*
    1. Extracts text from all files using PdfExtractionService
    2. Parse the extracted text using ResultParserService (REGEX)
    3. Create Student and Subjects entities and save them to the database
    4. Collect all Student entities and pass them to CreateExcelService to generate the Excel file
     */
    public byte[] processResults(String jobId) {
        PdfExtractionService pdfExtractionService = new PdfExtractionService(filejobRepository);

        //maybe try-catch here
        List<String> extractedFilesList = pdfExtractionService.extractTextFromAllFiles(jobId);

        List<Student> allStudentEntities = new ArrayList<>();

        for (String fileText : extractedFilesList) {
            ResultParserService resultParserService = new ResultParserService();

            //maybe try-catch here
            Map<String, Object> parsedResult = resultParserService.parseResult(fileText);

            Student studentEntity = new Student();
            studentEntity.setName((String) parsedResult.get("name"));
            studentEntity.setPrn((String) parsedResult.get("prn"));
            studentEntity.setSgpa(Double.parseDouble((String) parsedResult.get("sgpa")));
            studentEntity.setSubjects(new ArrayList<>());
            studentEntity.setJobId(jobId);

            List<String> allSubjects = (List<String>) parsedResult.get("subjectSection");
            for (String subject : allSubjects) {
                Subjects subjectsEntity = new Subjects();
                subjectsEntity.setSubjectName(subject.split(" - ")[0]);        //[SEMINAR - C]
                subjectsEntity.setGrade(subject.split(" - ")[1]);
                subjectsEntity.setStudent(studentEntity);
                studentEntity.getSubjects().add(subjectsEntity);
            }

            studentRepository.save(studentEntity);
            allStudentEntities.add(studentEntity);

        }
        CreateExcelService createExcel = new CreateExcelService();
        return createExcel.createExcelFile(allStudentEntities);
    }
}