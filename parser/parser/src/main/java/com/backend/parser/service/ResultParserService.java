package com.backend.parser.service;


import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//
//0:
//"Branch/Course: T.E.(2019 Credit Pattern) Winter Session 2025\r\nSeat No: T400220363 Center: [CEGP011130] \r\n[22]\r\nPerm Reg No(PRN): 72230475H\r\nStudent Name: GURRAPUSALA CHETAN KUMAR RAJU Mother Name: GURRAPUSALA \r\nARUNA DEVI\r\nCollege Name: [CEGP011130] [22] ARMY INSTITUTE OF TECHNOLOGY\r\nSem SubCode Subject Name Crd Grd GP\r\n5 314441 THEORY OF COMPUTATION 3     C 15\r\n314442 OPERATING SYSTEMS 3#     B 18\r\n314443 MACHINE LEARNING 3     B+ 21\r\n314444 HUMAN COMPUTER INTERACTION 3     C 15\r\n314445B ADVANCED DATABASE MGMT SYS 3     B+ 21\r\n314446 OPERATING SYSTEMS LAB(TW+PR) 2$     C 10\r\n314447 HUMAN COMP. INTERACTION-LAB. 1     A+ 09\r\n314448 LABORATORY PRACTICE-I 2     A 16\r\n314449 SEMINAR 1     O 10\r\n314450A BANKING AND INSURANCE 0     P 00\r\nTOTAL CREDITS EARNED : 21 SGPA 1 :- 6.43\r\nRESULT DATE:  10 February 2026\r\nThe results published online are for immediate information only. These cannot be treated as original statement \r\nof marks,Please verify the information from original statement of marks issued by the Savitribai Phule Pune \r\nUniversity separately.\r\n1 of 1   Download Date:28/03/2026Savitribai Phule Pune University,Online Result\r\nSAVITRIBAI PHULE PUNE UNIVERSITY\r\n(Formerly University of Pune)\r\nGANESHKHIND,PUNE 411007\r\n"
//1:
//"Branch/Course: T.E.(2019 Credit Pattern) Winter Session 2025\r\nSeat No: T400220343 Center: [CEGP011130] \r\n[22]\r\nPerm Reg No(PRN): 72306600M\r\nStudent Name: ANUBHAV TYAGI Mother Name: RASHMI TYAGI\r\nCollege Name: [CEGP011130] [22] ARMY INSTITUTE OF TECHNOLOGY,PUNE\r\nSem SubCode Subject Name Crd Ern\r\nCrd\r\nGrd GP Crd \r\nPnt\r\n5 * 314441 THEORY OF COMPUTATION 3 0     F 0 0\r\n* 314442 OPERATING SYSTEMS 3 3     A 8 24\r\n* 314443 MACHINE LEARNING 3 3     A+ 9 27\r\n* 314444 HUMAN COMPUTER INTERACTION 3 3     B+ 7 21\r\n* 314445B ADVANCED DATABASE MANAGEMENT \r\nSYSTEM\r\n3 3     A 8 24\r\n* 314446 OPERATING SYSTEMS LAB(TW+PR) 2 2     A 8 16\r\n* 314447 HUMAN COMPUTER INTERACTION- LAB 1 1     O 10 10\r\n* 314448 LABORATORY PRACTICE-I 2 2     A+ 9 18\r\n* 314449 SEMINAR 1 1     O 10 10\r\n* 314450A BANKING AND INSURANCE     AC\r\nFifth Semester SGPA : ----- Credits Earned/Total : 18/21 Total Credit Points: 150 \r\nRESULT DATE:  10 February 2026\r\nSUB:INFORMATION TECHNOLOGY\r\nThe results published online are for immediate information only. These cannot be treated as original statement \r\nof marks,Please verify the information from original statement of marks issued by the Savitribai Phule Pune \r\nUniversity separately.\r\n1 of 1   Download Date:24/03/2026Savitribai Phule Pune University,Online Result\r\nSAVITRIBAI PHULE PUNE UNIVERSITY\r\n(Formerly University of Pune)\r\nGANESHKHIND,PUNE 411007\r\n"
//2:
//"Branch/Course: T.E.(2019 Credit Pattern) Winter Session 2025\r\nSeat No: T400220333 Center: [CEGP011130] \r\n[22]\r\nPerm Reg No(PRN): 72306550M\r\nStudent Name: ABHISHEK KUMAR Mother Name: MEERA YADAV\r\nCollege Name: [CEGP011130] [22] ARMY INSTITUTE OF TECHNOLOGY,PUNE\r\nSem SubCode Subject Name Crd Ern\r\nCrd\r\nGrd GP Crd \r\nPnt\r\n5 * 314441 THEORY OF COMPUTATION 3 3     A+ 9 27\r\n* 314442 OPERATING SYSTEMS 3 3     O 10 30\r\n* 314443 MACHINE LEARNING 3 3     A+ 9 27\r\n* 314444 HUMAN COMPUTER INTERACTION 3 3     O 10 30\r\n* 314445B ADVANCED DATABASE MANAGEMENT \r\nSYSTEM\r\n3 3     O 10 30\r\n* 314446 OPERATING SYSTEMS LAB(TW+PR) 2 2     O 10 20\r\n* 314447 HUMAN COMPUTER INTERACTION- LAB 1 1     O 10 10\r\n* 314448 LABORATORY PRACTICE-I 2 2     O 10 20\r\n* 314449 SEMINAR 1 1     O 10 10\r\n* 314450A BANKING AND INSURANCE     AC\r\nFifth Semester SGPA : 9.71 Credits Earned/Total : 21/21 Total Credit Points: 204 \r\nRESULT DATE:  10 February 2026\r\nSUB:INFORMATION TECHNOLOGY\r\nThe results published online are for immediate information only. These cannot be treated as original statement \r\nof marks,Please verify the information from original statement of marks issued by the Savitribai Phule Pune \r\nUniversity separately.\r\n1 of 1   Download Date:24/03/2026Savitribai Phule Pune University,Online Result\r\nSAVITRIBAI PHULE PUNE UNIVERSITY\r\n(Formerly University of Pune)\r\nGANESHKHIND,PUNE 411007\r\n"
//3:
//"Branch/Course: T.E.(2019 Credit Pattern) Winter Session 2025\r\nSeat No: T400220330 Center: [CEGP011130] \r\n[22]\r\nPerm Reg No(PRN): 72306540D\r\nStudent Name: AAYUSHYA TIWARI Mother Name: ANUPMA DEVI\r\nCollege Name: [CEGP011130] [22] ARMY INSTITUTE OF TECHNOLOGY,PUNE\r\nSem SubCode Subject Name Crd Ern\r\nCrd\r\nGrd GP Crd \r\nPnt\r\n5 * 314441 THEORY OF COMPUTATION 3 3     C 5 15\r\n* 314442 OPERATING SYSTEMS 3 3     B+ 7 21\r\n* 314443 MACHINE LEARNING 3 3     A+ 9 27\r\n* 314444 HUMAN COMPUTER INTERACTION 3 3     A 8 24\r\n* 314445B ADVANCED DATABASE MANAGEMENT \r\nSYSTEM\r\n3 3     A 8 24\r\n* 314446 OPERATING SYSTEMS LAB(TW+PR) 2 2     O 10 20\r\n* 314447 HUMAN COMPUTER INTERACTION- LAB 1 1     A+ 9 9\r\n* 314448 LABORATORY PRACTICE-I 2 2     A+ 9 18\r\n* 314449 SEMINAR 1 1     O 10 10\r\n* 314450A BANKING AND INSURANCE     AC\r\nFifth Semester SGPA : 8.00 Credits Earned/Total : 21/21 Total Credit Points: 168 \r\nRESULT DATE:  10 February 2026\r\nSUB:INFORMATION TECHNOLOGY\r\nThe results published online are for immediate information only. These cannot be treated as original statement \r\nof marks,Please verify the information from original statement of marks issued by the Savitribai Phule Pune \r\nUniversity separately.\r\n1 of 1   Download Date:24/03/2026Savitribai Phule Pune University,Online Result\r\nSAVITRIBAI PHULE PUNE UNIVERSITY\r\n(Formerly University of Pune)\r\nGANESHKHIND,PUNE 411007\r\n"
//

public class ResultParserService {
    public String clean(String text){
      /*
        \r -> carriage return
        \n -> new line
        \s -> whitespace character (space, tab, etc.)
      */
        return text
                .replaceAll("\\r", "")
                .replaceAll("\\n+", "\n")
                .replaceAll("\\s{2,}", " ")
                .replaceAll("[*#$]", "");
    }

    public String extract(String regex, String text){
        Matcher m = Pattern.compile(regex).matcher(text);
        return m.find() ? m.group(1).trim() : null;

        /*
        What this do: if we can find the regex in text then return first group
                    first group means part of regex in ()
                    e.g. we have 'SGPA([0-9.]+)' and text is 'SGPA : 8.00' then it will return 8.00
         */
    }

    public String sgpaExtract(String text){
        String result = extract("SGPA.*?:.*?([0-9.\\-\\s{0,1}]+)", text);
        if (result == null || result.equals("-----")){
            return "0.00";
        }
        return result.contains("- ") ? result.replaceAll("- ", "") : result;
    }

    public Map<String, Object> parseResult(String text){
        String cleanedText = clean(text);
        String name = extract("Student Name: (.*?) Mother Name:", cleanedText);
        String sgpa = sgpaExtract(cleanedText);
        String prn = extract("Perm Reg No\\(PRN\\):\\s*(.*?)\\s*Student Name:", cleanedText);

        String subjectSectionText = subjectSectionExtract(cleanedText);
        List<String> subjects = subjectExtract(clean(subjectSectionText));
        String auditCourse = subjects.removeLast();

        return Map.of(
                "name", name,
                "prn", prn,
                "sgpa", sgpa,
                "subjectSection", subjects
        );
    }

    public String subjectSectionExtract(String text){
        //If I split a text, I get 2 parts, 0 means part before the regex string & 1 means after.

        String splitText = text.split("Sem SubCode")[1];
        return splitText.split("RESULT DATE")[0];

    }

    public List<String> subjectExtract(String text){
        String regex = "\\d{6}[A-Z]?\\s([A-Z\\+\\-\\.\\(\\)\\s?]+).*?([A-Z]{1,2}\\+?)";

        //Trim: To remove any trailing spaces from subject name because of \\s, one word subject will suffer
        Matcher m = Pattern.compile(regex).matcher(text);
        List<String> subjects = new java.util.ArrayList<>();
        while (m.find()){
            subjects.add(m.group(1).trim() + " - " + m.group(2).trim());
        }
        return subjects;
    }

}
