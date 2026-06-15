# AI Resume Analyzer

An AI-powered web application that analyzes resumes, calculates ATS scores, identifies skill gaps, and generates personalized improvement recommendations using Google Gemini.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2 |
| Database | PostgreSQL 15 |
| PDF Parsing | Apache PDFBox 3.0 |
| AI | Google Gemini API |
| Frontend | React 18 |
| DevOps | Docker, Docker Compose |

## Features

- 📄 **PDF Resume Parsing** — Extracts name, email, phone, skills, education, experience, projects
- 📊 **ATS Scoring** — Compares resume against job description keywords (0–100 score)
- 🔍 **Skill Gap Analysis** — Identifies missing skills with AI-powered recommendations
- 🤖 **AI Suggestions** — Gemini-generated resume improvement tips
- 💼 **Job Role Matching** — Recommends suitable roles based on candidate profile

## Quick Start

### Prerequisites
- Java 17+, Maven, Node.js 18+, Docker

### 1. Clone and configure

```bash
git clone <your-repo-url>
cd resume-analyzer
```

Set your Gemini API key:
```bash
export GEMINI_API_KEY=your_key_here
# Get a free key at: https://makersuite.google.com/app/apikey
```

### 2. Run with Docker Compose (recommended)

```bash
docker-compose up --build
```

Backend: http://localhost:8080  
Frontend: run separately (see below)

### 3. Run backend locally

```bash
cd backend
mvn spring-boot:run
```

### 4. Run frontend

```bash
cd frontend
npm install
npm start
```

Open http://localhost:3000

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/resume/upload` | Upload PDF resume |
| GET | `/api/resume/{id}` | Get parsed resume |
| POST | `/api/resume/{id}/analyze` | Run ATS + skill gap analysis |
| GET | `/api/resume/{id}/suggestions` | Get AI suggestions |

### Example: Upload Resume
```bash
curl -X POST http://localhost:8080/api/resume/upload \
  -F "file=@your-resume.pdf"
```

### Example: Analyze Resume
```bash
curl -X POST http://localhost:8080/api/resume/1/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "jobDescription": "We are looking for a Java developer...",
    "requiredSkills": ["Java", "Spring Boot", "Docker", "PostgreSQL"]
  }'
```

## Project Structure

```
resume-analyzer/
├── backend/
│   ├── src/main/java/com/resumeanalyzer/
│   │   ├── controller/     # REST endpoints
│   │   ├── service/        # Business logic (PDF parsing, AI calls)
│   │   ├── model/          # JPA entities
│   │   ├── repository/     # Spring Data JPA
│   │   └── dto/            # Request/Response objects
│   └── pom.xml
├── frontend/
│   └── src/
│       ├── App.jsx         # Main React app (Upload + Dashboard)
│       └── services/api.js # API client
└── docker-compose.yml
```

## Resume Bullet Points (for your resume)

- Developed a full-stack AI web application using **Spring Boot**, **React**, and **PostgreSQL** that analyzes resumes and calculates ATS compatibility scores
- Integrated **Google Gemini API** to generate personalized skill gap analysis and actionable resume improvement recommendations
- Built a **PDF parsing pipeline** using Apache PDFBox to extract structured data (skills, education, experience) from uploaded resumes
- Implemented **ATS scoring algorithm** that matches candidate skills against job requirements with configurable keyword weighting
- Containerized the full application stack with **Docker Compose**, enabling one-command deployment

## License

MIT
