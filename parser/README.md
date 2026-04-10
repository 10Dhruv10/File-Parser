# Parser — Result Parser (Spring Boot)

A Spring Boot 3 REST backend that receives uploaded university result PDFs, extracts structured student data using Apache PDFBox and regex parsing, persists it in MySQL via JPA/Flyway, and returns a generated Excel file using Apache POI.

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Spring Boot | 3.5 | Application framework |
| Spring Data JPA | — | ORM / database access |
| MySQL | 8+ | Relational database |
| Flyway | — | Database schema migrations |
| Apache PDFBox | 2.0.30 | PDF text extraction |
| Apache POI (OOXML) | 5.2.5 | Excel (.xlsx) generation |
| Lombok | — | Boilerplate reduction |
| Maven | 3.8+ | Build tool |

---

## Project Structure

```
parser/
└── parser/
    ├── src/main/
    │   ├── java/com/backend/parser/
    │   │   ├── ParserApplication.java         # Entry point
    │   │   ├── controller/
    │   │   │   └── uploadController.java      # POST /backendApi/upload
    │   │   ├── dto/
    │   │   │   └── UploadResponse.java
    │   │   ├── entities/
    │   │   │   ├── Filejob.java               # Upload batch (job)
    │   │   │   ├── File.java                  # Individual uploaded file
    │   │   │   ├── Student.java               # Parsed student record
    │   │   │   └── Subjects.java              # Per-subject grade record
    │   │   ├── repository/
    │   │   │   ├── FilejobRepository.java
    │   │   │   ├── FileRepository.java
    │   │   │   ├── StudentRepository.java
    │   │   │   └── SubjectsRepository.java
    │   │   └── service/
    │   │       ├── PdfExtractionService.java  # Raw text extraction (PDFBox)
    │   │       ├── ResultParserService.java   # Regex-based structured parsing
    │   │       ├── ResultProcessingService.java # Orchestration
    │   │       └── CreateExcelService.java    # Excel generation (Apache POI)
    │   └── resources/
    │       ├── application.yaml
    │       └── db/migration/                  # Flyway SQL scripts
    │           ├── V1__add_file_job_table.sql
    │           ├── V2__add_file_table.sql
    │           ├── V3__add_student_table.sql
    │           └── V4__add_subjects_table.sql
    ├── pom.xml
    └── mvnw / mvnw.cmd
```

---

## Prerequisites

- **Java 21+**
- **Maven 3.8+** (or use the included `./mvnw` wrapper)
- **MySQL 8+** running on `localhost:3306`

---

## Setup & Running

### 1. Configure the database

Edit `src/main/resources/application.yaml` with your MySQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/parserDB?createDatabaseIfNotExist=true
    username: root
    password: root
```

The `parserDB` database is created automatically on first start. Schema tables are managed by Flyway and applied automatically at startup.

### 2. Start the application

```bash
cd parser/parser
./mvnw spring-boot:run
```

The backend starts on **http://localhost:8080**.

### Build a JAR

```bash
./mvnw package
java -jar target/parser-0.0.1-SNAPSHOT.jar
```

---

## API

### `POST /backendApi/upload`

Accepts one or more PDF files and returns a parsed Excel file.

**Request**

| Property | Value |
|---|---|
| Content-Type | `multipart/form-data` |
| Field name | `filesFromAngular` (repeat the field for multiple files) |
| Max file size | 50 MB per file |
| Max request size | 50 MB total |

**Response**

| Property | Value |
|---|---|
| Content-Type | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` |
| Content-Disposition | `attachment; filename=results.xlsx` |
| Body | Binary `.xlsx` file |

**Error responses**

| Status | Condition |
|---|---|
| 400 | No files included in the request |
| 500 | I/O error reading file bytes |

CORS is enabled for `http://localhost:4200`.

---

## Processing Pipeline

```
POST /backendApi/upload
        │
        ▼
uploadController
  • Creates a Filejob (UUID job ID, status=PENDING, totalFiles count)
  • Saves each file's bytes, name, type, and size as a File entity
  • Persists job + files in a single transaction (cascade)
        │
        ▼
PdfExtractionService  ──▶  Apache PDFBox extracts raw text from each PDF
        │
        ▼
ResultParserService   ──▶  Regex parsing extracts:
                             • Student name
                             • PRN (Permanent Registration Number)
                             • SGPA
                             • Subject names and grades
        │
        ▼
ResultProcessingService ──▶ Saves Student + Subjects entities to MySQL 
        │
        ▼
CreateExcelService    ──▶  Apache POI builds a .xlsx workbook
                           One row per subject per student
        │
        ▼
Returns binary .xlsx response to the caller
```

---

## Database Schema

Four tables are created by Flyway migrations:

| Table | Description |
|---|---|
| `file_job` | One record per upload batch; holds job ID, status, and file count |
| `file` | One record per uploaded file; stores raw bytes and metadata |
| `student` | One record per parsed student; holds name, PRN, and SGPA |
| `subjects` | One record per subject per student; holds subject name and grade |

---

## Excel Output Format

| Column | Content |
|---|---|
| B | Student Name |
| C | PRN |
| D | SGPA |
| E | Subject Name |
| F | Grade |

One row is written for each subject of each student.

---

## Configuration Reference

All settings are in `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: parser

  datasource:
    url: jdbc:mysql://localhost:3306/parserDB?createDatabaseIfNotExist=true
    username: root          # Change for your environment
    password: root          # Change for your environment

  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
```
