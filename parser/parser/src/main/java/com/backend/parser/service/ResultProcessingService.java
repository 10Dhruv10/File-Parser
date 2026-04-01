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

    public byte[] processResults(String jobId) {
        PdfExtractionService pdfExtractionService = new PdfExtractionService(filejobRepository);
        List<String> extractedFilesList = pdfExtractionService.extractTextFromAllFiles(jobId);
        List<Student> allStudentEntities = new ArrayList<>();

        for (String fileText : extractedFilesList) {
            ResultParserService resultParserService = new ResultParserService();
            Map<String, Object> parsedResult = resultParserService.parseResult(fileText);

            Student studentEntity = new Student();
            studentEntity.setName((String) parsedResult.get("name"));
            studentEntity.setPrn((String) parsedResult.get("prn"));
            studentEntity.setSgpa(Double.parseDouble((String) parsedResult.get("sgpa")));
            studentEntity.setSubjects(new ArrayList<>());

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