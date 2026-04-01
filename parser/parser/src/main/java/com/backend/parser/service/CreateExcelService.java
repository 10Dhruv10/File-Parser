package com.backend.parser.service;

import com.backend.parser.entities.Student;
import com.backend.parser.entities.Subjects;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CreateExcelService {

    public byte[] createExcelFile(List<Student> students){
        //XSSFWorkbook allocates resources, so we use try-with-resources to ensure it gets closed properly
        try (Workbook workbook = new XSSFWorkbook()){
            Sheet sheet = workbook.createSheet("MyExcel");

            //Making Columns
            Row header = sheet.createRow(0);
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("PRN");
            header.createCell(3).setCellValue("SGPA");
            header.createCell(4).setCellValue("Subject");
            header.createCell(5).setCellValue("Grade");

            //Filling Rows from 1
            int rowIndex = 1;
            for(Student student: students){
                for (Subjects subject: student.getSubjects()){
                    Row row = sheet.createRow(rowIndex++);

                    row.createCell(1).setCellValue(student.getName());
                    row.createCell(2).setCellValue(student.getPrn());
                    row.createCell(3).setCellValue(student.getSgpa());
                    row.createCell(4).setCellValue(subject.getSubjectName());
                    row.createCell(5).setCellValue(subject.getGrade());
                }
            }

            //An empty buffer container is made into which all workbook data is written.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);

            //The byte array output stream is converted to a byte array
            return output.toByteArray();

        }
        catch(Exception e){
            throw new RuntimeException("Error creating excel file", e);
        }
    }
}

//PDFs, JOB-ID create, Added in File-FileJob,
//PDFS process, Added in Student-Subject