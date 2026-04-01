# File Parser

A full-stack web application that parses university result PDFs (Savitribai Phule Pune University) and exports the extracted student data like name, PRN, SGPA, and subject-wise grades->into a downloadable Excel file.

---

## Features

- Upload one or more PDF result files via the UI
- Preview uploaded PDFs and images directly.
- Automatically extract student name, PRN, SGPA, and per-subject grades from each PDF
- Export all parsed results as a single `.xlsx` file
- Track each upload batch as a job stored in a MySQL database

---

## Tech Stack

| Layer     | Technology                                                         |
|-----------|--------------------------------------------------------------------|
| Frontend  | Angular 19, Angular Material, ngx-extended-pdf-viewer              |
| Backend   | Java 21, Spring Boot 3, Apache PDFBox      |
| Database  | MySQL (Flyway migrations)                                          |
| Excel     | Apache POI (OOXML)                                                 |
| Build     | Maven (backend), npm / Angular CLI (frontend)                      |

---

## Project Structure

```
File-Parser/
├── frontend/
│   └── result-parser/        # Angular 19 application
│       └── src/app/
│           └── upload-file/  # File upload & preview component
└── parser/
    └── parser/               # Spring Boot application
        └── src/main/
            ├── java/com/backend/parser/
            │   ├── controller/       # REST endpoint (/backendApi/upload)
            │   ├── service/          # PDF extraction, parsing, Excel generation
            │   ├── entities/         # JPA entities (Filejob, File, Student, Subjects)
            │   └── repository/       # Spring Data JPA repositories
            └── resources/
                ├── application.yaml
                └── db/migration/     # Flyway SQL migration scripts
```

---

## Prerequisites

- **Java 21+**
- **Maven 3.8+**
- **Node.js 18+** and **npm**
- **Angular CLI** (`npm install -g @angular/cli`)
- **MySQL** running locally on port `3306`

---

## Setup & Running

### 1. Database

Ensure MySQL is running. The application will create the `parserDB` database automatically on first start (via `createDatabaseIfNotExist=true`). Schema tables are managed by Flyway migrations.

Default credentials (edit `application.yaml` to change):
```
username: root
password: root
```

### 2. Backend

```bash
cd parser/parser
./mvnw spring-boot:run
```

The backend starts on **http://localhost:8080**.

To change the MySQL username/password, edit `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/parserDB?createDatabaseIfNotExist=true
    username: root
    password: root
```

### 3. Frontend

```bash
cd frontend/result-parser
npm install
ng serve
```

The frontend starts on **http://localhost:4200**.

---

## Usage

1. Open **http://localhost:4200** in your browser.
2. Click the **📎 attach file** button to select one or more PDF result files.
3. Preview each file using the arrow buttons or by entering an index number.
4. Click **Submit & Download** to send the files to the backend.
5. A progress bar tracks the upload. Once processing is complete, `results.xlsx` is automatically downloaded.

---

## How It Works

```
Upload PDFs (Angular)
       │
       ▼
POST /backendApi/upload  ──▶  Save Filejob + Files to MySQL
       │
       ▼
PdfExtractionService  ──▶  Extract raw text from each PDF (Apache PDFBox)
       │
       ▼
ResultParserService   ──▶  Regex-based parsing
                            • Student Name
                            • PRN
                            • SGPA
                            • Subject names + grades
       │
       ▼
Save Students + Subjects to MySQL
       │
       ▼
CreateExcelService    ──▶  Build .xlsx with Apache POI
       │
       ▼
Return .xlsx as binary response  ──▶  Browser auto-downloads results.xlsx
```

---

## API

| Method | Endpoint              | Description                          |
|--------|-----------------------|--------------------------------------|
| POST   | `/backendApi/upload`  | Accepts multipart files, returns `.xlsx` |

**Request:** `multipart/form-data` with field name `filesFromAngular` (supports multiple files).

**Response:** `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` — the generated Excel file.

---

## Supported File Formats

- **PDF** – primary format; text is extracted and parsed
- **JPEG / PNG** – can be uploaded and previewed in the UI (image parsing is not yet implemented in the backend)

---

## Excel Output Format

| Column | Content      |
|--------|--------------|
| B      | Student Name |
| C      | PRN          |
| D      | SGPA         |
| E      | Subject Name |
| F      | Grade        |

One row is written per subject per student.

## Methodology
Once user submits the PDFs, they will be saved in mysql database (tables fileJob & File) under one single jobID, all files in this jobID will then be analyzed by apache pdfBOX and this semi-structured data will be extracted and then sent for Parsing, which will be done with help of REGEX.
Then this extracted info will be stored under 2 tables students and subjects, Apache POII will create excel file from this fresh information stored in these 2 tables.

## Future (To-do):
- As of now I can upload images and preview them, but they won't be analyzed so remove that image thingy on angular side (as its only there)
- Lots of edge cases exist as of now, identify what they are.
- Create Session for users, each session will last 15minutes and after that all the data of that job ID will be removed from DB
- Add an AI agent to generate insights from this PDF.
- Finally Deploy the project
