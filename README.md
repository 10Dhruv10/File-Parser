# File Parser

A full-stack web application that parses Savitribai Phule Pune University result PDFs, stores structured student records, generates downloadable Excel files, and forwards processed student data to a local AI/agent endpoint.

---

## Features

- Upload one or more result PDFs from the Angular UI
- Input validations before processing:
  - Reject empty uploads
  - Limit batch size to **130 files**
  - Validate each PDF structure and page count (must be 1–2 pages)
  - Reject files larger than **200 KB**
- Preview uploaded PDFs in-app with index-based navigation
- Parse each PDF and extract:
  - Student name
  - PRN
  - SGPA (with fallback handling for missing/blank values)
  - Subject-wise name and grade
- Persist processing data in MySQL as:
  - Job (`Filejob`)
  - Uploaded files (`File`)
  - Parsed student records (`Student`)
  - Subject rows (`Subjects`)
- Export parsed data as a single `.xlsx` file
- Return `jobId` in response headers for frontend usage
- Send parsed student JSON payload to local endpoint: `http://127.0.0.1:8000/studentData`
- Scheduled cleanup removes stale jobs and associated student records every 5 minutes (older than 30 minutes)

---

## Tech Stack

| Layer     | Technology |
|-----------|------------|
| Frontend  | Angular 19, Angular Material, ngx-extended-pdf-viewer |
| Backend   | Java 21, Spring Boot 3, Apache PDFBox |
| Database  | MySQL with Flyway migrations |
| Excel     | Apache POI (XSSF) |
| Build     | Maven (backend), npm / Angular CLI (frontend) |

---

## Project Structure

```text
File-Parser/
├── frontend/
│   └── result-parser/
│       └── src/app/
│           ├── upload-file/      # Upload UI + PDF preview + submit/download
│           ├── agent-chat/       # Chat/agent screen (navigated to after upload)
│           └── service/          # Shared services (e.g., jobId service)
├── parser/
│   └── parser/
│       └── src/main/
│           ├── java/com/backend/parser/
│           │   ├── controller/   # REST endpoints
│           │   ├── service/      # PDF extraction, parsing, Excel, scheduler, REST client
│           │   ├── entities/     # JPA entities
│           │   ├── repository/   # Spring Data repositories
│           │   └── dto/          # Data transfer objects
│           └── resources/
│               ├── application.yaml
│               └── db/migration/ # Flyway SQL scripts
└── Ai Agent/
    └── ...                       # Additional AI/agent-related code/resources
```

---

## Prerequisites

- **Java 21+**
- **Maven 3.8+**
- **Node.js 18+** and **npm**
- **Angular CLI** (`npm install -g @angular/cli`)
- **MySQL** running on `localhost:3306`

---

## Setup & Run

### 1) Database

Ensure MySQL is running. The backend uses:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/parserDB?createDatabaseIfNotExist=true
    username: root
    password: root
```

Flyway will manage schema migrations automatically.

### 2) Backend (Spring Boot)

```bash
cd parser/parser
./mvnw spring-boot:run
```

Backend URL: `http://localhost:8080`

### 3) Frontend (Angular)

```bash
cd frontend/result-parser
npm install
ng serve
```

Frontend URL: `http://localhost:4200`

---

## Usage

1. Open `http://localhost:4200`.
2. Click the attach button and select one or more result PDFs.
3. Preview selected files and navigate using arrows/index input.
4. Click **Submit & Download**.
5. Upload progress is shown in a progress bar.
6. On completion:
   - `results.xlsx` is downloaded automatically
   - frontend stores `jobId` from response header
   - UI navigates to `/agentchat`

---

## API

### `POST /backendApi/upload`

Accepts multiple files using `multipart/form-data`.

- **Field name:** `filesFromAngular`
- **Validation rules:**
  - At least one file
  - Maximum 130 files
  - Each file <= 200 KB
  - Valid PDF with 1–2 pages

**Success response**

- Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- Content-Disposition: `attachment; filename=results.xlsx`
- Header: `jobId: <generated-uuid>`
- Body: generated `.xlsx` bytes

---

## Processing Flow

```text
Angular upload UI
   │
   ▼
POST /backendApi/upload
   │
   ├─ Validate files (count/size/pdf)
   ├─ Create Filejob + File entries
   ▼
ResultProcessingService
   ├─ PdfExtractionService: extract text from each PDF
   ├─ ResultParserService: regex parse (name, PRN, SGPA, subjects)
   ├─ Save Student + Subjects rows
   └─ CreateExcelService: generate results.xlsx
   ▼
Return Excel response + jobId header
   │
   └─ initiateSendingStudentData(jobId)
       └─ restClientService -> POST http://127.0.0.1:8000/studentData
```

---

## Excel Output

One row per **subject per student**.

| Column | Value |
|--------|-------|
| B | Student Name |
| C | PRN |
| D | SGPA |
| E | Subject Name |
| F | Grade |

---

## Scheduled Cleanup

`ScheduledDeletionService` runs every 5 minutes and deletes jobs older than 30 minutes, along with associated student data, to keep the database clean.

---

## Notes

- This parser is tailored to the current SPPU result PDF layout. Regex rules may need updates if the university changes result formatting.
- Backend currently processes PDFs; frontend rejects non-PDF during upload validation.
