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
    private final FileRepository fileRepository;
    private final StudentRepository studentRepository;

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

            ResultParserService resultParserService = new ResultParserService();
            Map<String, Object> parsedResult = resultParserService.parseResult(text);

            Student studentEntity = new Student();
            studentEntity.setName((String) parsedResult.get("name"));
            studentEntity.setPrn((String) parsedResult.get("prn"));
            studentEntity.setSgpa(Double.parseDouble((String) parsedResult.get("sgpa")));
            studentEntity.setSubjects(new ArrayList<>());

            List<String> allSubjects = (List<String>) parsedResult.get("subjectSection");
            for (String subject : allSubjects) {
                Subjects subjectsEntity = new Subjects();
//                [THEORY OF COMPUTATION - C, OPERATING SYSTEMS - B, MACHINE LEARNING - B+, HUMAN COMPUTER INTERACTION - C, ADVANCED DATABASE MGMT SYS - B+, OPERATING SYSTEMS LAB(TW+PR) - C, HUMAN COMP. INTERACTION-LAB. - A+, LABORATORY PRACTICE-I - A, SEMINAR - O]
                subjectsEntity.setSubjectName(subject.split(" - ")[0]);
                subjectsEntity.setGrade(subject.split(" - ")[1]);
                subjectsEntity.setStudent(studentEntity);
                studentEntity.getSubjects().add(subjectsEntity);
            }

            studentRepository.save(studentEntity);

        }
        return filesList;
    }
}
//
//import java.util.*;
//import java.util.regex.*;
//
//public class ResultParser {
//
//    // ---------------- NORMALIZE ----------------
//    private String normalize(String text) {
//        return text
//                .replaceAll("\\r", "")
//                .replaceAll("[*#$]", "")          // remove symbols
//                .replaceAll("\\n+", "\n")
//                .replaceAll("\\s{2,}", " ");
//    }
//
//    public StudentResult parse(String rawText) {
//
//        String text = normalize(rawText);
//
//        String name = extract("Student Name: (.*?) Mother Name:", text);
//        Double sgpa = extractSGPA(text);
//
//        // -------- SUBJECT SECTION --------
//        String subjectSection = extractSubjectSection(text);
//
//        List<String> lines = preprocessLines(subjectSection);
//
//        for (String line : lines) {
//            Subject subject = parseSubject(line);
//            if (subject != null) {
//                result.subjects.add(subject);
//            }
//        }
//
//        return result;
//    }
//
//
//
//    // ---------------- EXTRACT GENERIC ----------------
//    private String extract(String regex, String text) {
//        Matcher m = Pattern.compile(regex).matcher(text);
//        return m.find() ? m.group(1).trim() : null;
//    }
//
//    // ---------------- SGPA ----------------
//    private Double extractSGPA(String text) {
//        String sgpaRaw = extract("SGPA.*?:\\s*([0-9.\\-]+)", text);
//
//        if (sgpaRaw == null || sgpaRaw.contains("-")) return null;
//
//        return Double.parseDouble(sgpaRaw);
//    }
//
//    // ---------------- SUBJECT SECTION ----------------
//    private String extractSubjectSection(String text) {
//        try {
//            String[] parts = text.split("Sem SubCode");
//            return parts[1].split("RESULT DATE")[0];
//
//        } catch (Exception e) {
//            return "";
//        }
//    }
//
//    // ---------------- FIX MULTILINE SUBJECTS ----------------
//    private List<String> preprocessLines(String section) {
//
//        // join broken lines (IMPORTANT)
//        section = section.replaceAll("\\n(?!\\s*\\d)", " ");
//
//        String[] rawLines = section.split("\\n");
//
//        List<String> lines = new ArrayList<>();
//
//        for (String line : rawLines) {
//            line = line.trim();
//
//            // keep only lines that contain subject code
//            if (line.matches(".*\\d{6}[A-Z]?.*")) {
//                lines.add(line);
//            }
//        }
//
//        return lines;
//    }
//
//    // ---------------- PARSE SINGLE SUBJECT ----------------
//    private Subject parseSubject(String line) {
//
//        // extract subject code
//        Matcher codeMatcher = Pattern.compile("(\\d{6}[A-Z]?)").matcher(line);
//
//        if (!codeMatcher.find()) return null;
//
//        String code = codeMatcher.group(1);
//
//        String afterCode = line.substring(line.indexOf(code) + code.length()).trim();
//
//        String[] tokens = afterCode.split("\\s+");
//
//        int creditIndex = findCreditIndex(tokens);
//
//        if (creditIndex == -1) return null;
//
//        String name = String.join(" ",
//                Arrays.copyOfRange(tokens, 0, creditIndex));
//
//        int credits = Integer.parseInt(tokens[creditIndex].replaceAll("[^0-9]", ""));
//
//        String grade = findGrade(tokens);
//
//        String type = detectType(name);
//
//        Subject subject = new Subject();
//        subject.code = code;
//        subject.name = name;
//        subject.grade = grade;
//        subject.credits = credits;
//        subject.type = type;
//
//        return subject;
//    }
//
//    // ---------------- FIND CREDIT ----------------
//    private int findCreditIndex(String[] tokens) {
//        for (int i = 0; i < tokens.length; i++) {
//            if (tokens[i].matches("\\d+")) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    // ---------------- FIND GRADE ----------------
//    private String findGrade(String[] tokens) {
//        for (String t : tokens) {
//            if (t.matches("O|A\\+|A|B\\+|B|C|F|P")) {
//                return t;
//            }
//        }
//        return null;
//    }
//
//    // ---------------- DETECT TYPE ----------------
//    private String detectType(String name) {
//        String n = name.toLowerCase();
//
//        if (n.contains("lab") || n.contains("pr") || n.contains("tw")) {
//            return "PRACTICAL";
//        }
//
//        return "THEORY";
//    }
//}
