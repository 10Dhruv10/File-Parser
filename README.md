# File Parser

A full-stack web application that parses Savitribai Phule Pune University result PDFs, stores structured student records, generates downloadable Excel files, and provides an AI-powered chat interface to query the parsed data.

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
- Send parsed student JSON payload to the AI Agent: `http://127.0.0.1:8000/studentData`
- AI Agent stores student data in Redis (TTL: 30 minutes) keyed by `jobId`
- AI-powered chat interface (post-upload) backed by Google Gemini (`gemini-2.5-flash`) with function-calling support
  - `get_top_students(n)` tool: returns the top *n* students sorted by SGPA descending
  - Gemini responses rendered as formatted Markdown in the Angular chat UI
- Scheduled cleanup removes stale jobs and associated student records every 5 minutes (older than 30 minutes)

---

## Tech Stack

| Layer     | Technology |
|-----------|------------|
| Frontend  | Angular 19, Angular Material, ngx-extended-pdf-viewer, marked |
| Backend   | Java 21, Spring Boot 3, Apache PDFBox |
| AI Agent  | Python 3, FastAPI, Google Gemini (`google-genai`), Redis |
| Database  | MySQL with Flyway migrations |
| Cache     | Redis (student data per job, 30-minute TTL) |
| Excel     | Apache POI (XSSF) |
| Build     | Maven (backend), npm / Angular CLI (frontend), uvicorn (AI Agent) |

---

## Project Structure

```text
File-Parser/
├── frontend/
│   └── result-parser/
│       └── src/app/
│           ├── upload-file/      # Upload UI + PDF preview + submit/download
│           │   └── job-id.service.ts  # Shared jobId signal service
│           └── agent-chat/       # AI chat UI (navigated to after upload)
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
    └── main.py                   # FastAPI server: /studentData + /chat endpoints
```

---

## Prerequisites

- **Java 21+**
- **Maven 3.8+**
- **Node.js 18+** and **npm**
- **Angular CLI** (`npm install -g @angular/cli`)
- **MySQL** running on `localhost:3306`
- **Python 3.10+** and **pip**
- **Redis** running on `localhost:6379`
- **Google Gemini API key** (set as `GEMINI_API_KEY` environment variable)

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

### 3) AI Agent (FastAPI)

```bash
cd "Ai Agent"
pip install fastapi uvicorn google-genai redis python-dotenv
```

Create a `.env` file in the `Ai Agent/` directory:

```env
GEMINI_API_KEY=your_google_gemini_api_key_here
```

Start the agent:

```bash
python main.py
```

AI Agent URL: `http://127.0.0.1:8000`

> **Note:** Redis must be running on `localhost:6379` before starting the AI Agent.

### 4) Frontend (Angular)

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
   - Frontend stores `jobId` from response header
   - UI navigates to `/agentchat`
7. In the chat screen, type natural-language queries about the uploaded student data (e.g., *"Who are the top 5 students?"*).
8. The AI Agent uses Google Gemini with function-calling to answer questions and returns formatted Markdown replies.

---

## API

### Spring Boot — `POST /backendApi/upload`

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

### AI Agent — `POST /studentData`

Receives parsed student data from Spring Boot and stores it in Redis.

**Request body**

```json
{
  "jobId": "<uuid>",
  "students": [
    {
      "name": "Student Name",
      "prn": "123456789",
      "sgpa": 8.5,
      "subjects": [
        { "subjectName": "Mathematics", "grade": "O" }
      ]
    }
  ]
}
```

**Response**

```json
{ "received jobId: ": "<uuid>" }
```

---

### AI Agent — `POST /chat`

Accepts a natural-language prompt from the Angular chat UI and returns a Gemini-generated reply.

**Request body**

```json
{
  "jobId": "<uuid>",
  "text": "Who are the top 3 students?"
}
```

**Response**

```json
{ "reply": "Here are the top 3 students by SGPA: ..." }
```

- Returns `404` if the `jobId` is not found in Redis (data expired or never received).
- Gemini may invoke the `get_top_students(n)` tool internally before producing a final answer.

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
                                   │
                                   └─ Store in Redis (key=jobId, TTL=1800s)

Angular navigates to /agentchat
   │
   ▼
User types query -> POST http://127.0.0.1:8000/chat
   │
   ├─ Load student data from Redis by jobId
   ├─ Send prompt + student context to Gemini (gemini-2.5-flash)
   ├─ Gemini may call get_top_students(n) tool
   └─ Return final Markdown reply to Angular
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

`ScheduledDeletionService` runs every 5 minutes and deletes jobs older than 30 minutes, along with associated student data, to keep the database clean. Redis entries expire automatically after 30 minutes via TTL.

---

## Notes

- This parser is tailored to the current SPPU result PDF layout. Regex rules may need updates if the university changes result formatting.
- Backend currently processes PDFs; frontend rejects non-PDF during upload validation.
- The AI Agent requires an active Redis instance and a valid `GEMINI_API_KEY` to function.
- Student data in Redis expires after 30 minutes, matching the MySQL cleanup interval.

## Future

- Add Tesseract OCR in Springboot as a fallback scanner if apache pdf box fails to extract pdf.
- Improve AgentChat Side UI
- Strictly reinforce tool calling on gemini side.
- User Guide Manual
- Deployment on Render
- Error Component in Angular Side (Error management for edge cases)
