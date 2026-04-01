# Frontend — Result Parser (Angular)

An Angular 19 single-page application that lets users upload university result PDFs, preview them in-browser, and download the parsed results as an Excel file.

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Angular | 19 | Application framework |
| Angular Material | 19 | UI components (buttons, icons, progress bar, inputs) |
| ngx-extended-pdf-viewer | 25 | In-browser PDF preview |
| RxJS | 7.8 | Reactive HTTP and event streams |
| TypeScript | 5.7 | Language |
| Karma + Jasmine | — | Unit testing |

---

## Project Structure

```
frontend/
└── result-parser/
    ├── src/
    │   ├── app/
    │   │   ├── upload-file/              # Main feature component
    │   │   │   ├── upload-file.component.ts
    │   │   │   ├── upload-file.component.html
    │   │   │   └── upload-file.component.css
    │   │   ├── app.component.*           # Root component
    │   │   ├── app.config.ts             # Application configuration
    │   │   └── app.routes.ts             # Route definitions
    │   ├── index.html
    │   ├── main.ts
    │   └── styles.css
    ├── angular.json
    ├── package.json
    └── tsconfig.json
```

---

## Prerequisites

- **Node.js 18+** and **npm**
- **Angular CLI** — install globally if not already present:
  ```bash
  npm install -g @angular/cli
  ```
- The backend (Spring Boot) must be running on `http://localhost:8080`

---

## Setup & Running

```bash
cd frontend/result-parser
npm install
ng serve
```

The app starts on **http://localhost:4200** and hot-reloads on file changes.

### Build for production

```bash
ng build
```

Output is placed in `dist/result-parser/`.

### Run unit tests

```bash
ng test
```

---

## Features

- **Multi-file upload** — select one or more PDF (or image) files at once.
- **In-browser preview** — view each selected PDF using `ngx-extended-pdf-viewer`; images are also rendered.
- **File navigation** — step through previews with arrow buttons or enter an index number directly.
- **Upload progress bar** — an Angular Material progress bar tracks upload progress in real time.
- **Auto-download** — once the backend finishes parsing, `results.xlsx` is automatically downloaded by the browser.
- **File type validation** — only `application/pdf`, `image/jpeg`, and `image/png` files are accepted.

---

## Component Overview

### `UploadFileComponent`

Located in `src/app/upload-file/`.

| Signal / Property | Type | Description |
|---|---|---|
| `filesUploaded` | `signal<File[]>` | Files selected by the user |
| `filePreview` | `signal<string[]>` | Object URLs for preview rendering |
| `selectedIndex` | `signal<number>` | Currently previewed file index |
| `progressValue` | `signal<number>` | Upload progress percentage (0–100) |

| Method | Description |
|---|---|
| `onFileUpload(event)` | Validates file types and creates preview URLs |
| `changeIndex(value)` | Increments / decrements the preview index |
| `changeIndexOnSelect(event)` | Jumps to a specific index entered in the input field |
| `onSubmit()` | Builds `FormData` and POSTs files to the backend |
| `ngOnDestroy()` | Revokes preview object URLs to free memory |

---

## Backend Communication

The component POSTs files to:

```
POST http://localhost:8080/backendApi/upload
Content-Type: multipart/form-data
Field name: filesFromAngular  (multiple values supported)
```

The response is a binary `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` blob which is saved as `results.xlsx`.

To point the app at a different backend, update the URL in `upload-file.component.ts`:

```ts
this.http.post("http://localhost:8080/backendApi/upload", formData, ...)
```

---

## Supported File Types

| Type | Upload | Preview | Backend parsing |
|---|---|---|---|
| PDF | ✅ | ✅ | ✅ |
| JPEG / PNG | ✅ | ✅ | ❌ (not yet implemented) |
