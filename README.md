# 🧭 Job Hunt Copilot MCP

> **A copilot for your job search: track applications, see what you're missing for each role and prepare for technical interviews, all from your AI client.**

**Job Hunt Copilot** is an **MCP (Model Context Protocol)** server built with **Spring Boot**, **Spring AI** and **Gemini**. Connect it to any MCP-compatible client (VS Code, Claude Desktop...) and manage your job search in natural language: paste a job posting and the server extracts its skills, stores it in PostgreSQL and helps you follow it through to the end.

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI_2-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Gemini](https://img.shields.io/badge/Gemini-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL_17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
[![Blog series](https://img.shields.io/badge/📝_Blog_series-franra18.github.io-2ea44f?style=for-the-badge)](https://franra18.github.io/blog/projects/job-hunt-mcp/)

> ℹ️ The MCP tool names and their responses are in Spanish.

---

## 📑 Table of Contents

- [Video](#-video)
- [Features](#-features)
- [MCP Tools](#️-mcp-tools)
- [Architecture](#️-architecture)
- [Tech Stack](#-tech-stack)
- [Data Model](#️-data-model)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Connecting an MCP Client](#-connecting-an-mcp-client)

---

## 🎥 Video


https://github.com/user-attachments/assets/65772638-d556-43ba-9f53-92cb965e1555


---

## ✨ Features

- **📝 Smart job intake:** paste a job description and Gemini extracts the **required** and **nice-to-have** skills with structured output before the application is saved.
- **🔄 Pipeline tracking:** `APPLIED` → `SCREENING` → `TECHNICAL_INTERVIEW` → `OFFER` / `REJECTED` / `WITHDRAWN`, with notes for each interview.
- **🎯 Skill-gap analysis:** compares each posting's requirements with your skill profile and tells you what you know, what you're missing and which nice-to-haves you meet.
- **🎤 Interview prep:** generates tough technical questions tailored to the role, with key answer points and a difficulty level, and saves them so you can review them later.
- **📊 Analytics:** application funnel by status and the technologies that come up most in the postings that rejected you.

---

## 🛠️ MCP Tools

| Tool | What it does |
|---|---|
| `analizar_y_registrar_oferta` | Extracts the posting's skills with AI and registers the application (status `APPLIED`) in one step. |
| `extraer_competencias_de_texto` | Analyses a posting's skills without saving anything, so you can review them first. |
| `actualizar_estado_proceso` | Changes an application's status and stores interview notes. |
| `obtener_candidatura` | Returns the full details of an application. |
| `listar_candidaturas` | Lists applications in summary form, optionally filtered by status. |
| `configurar_competencias_perfil` | Adds skills to your profile (or replaces it with `replaceExisting=true`). |
| `obtener_analisis_carencias` | Compares a posting with your profile: skills you have, gaps and nice-to-haves you meet. |
| `generar_preguntas_entrevista` | Generates and saves technical questions based on the role and its skills. |
| `obtener_preparacion_entrevista` | Retrieves the saved questions for an application. |
| `obtener_resumen_metricas` | Funnel metrics (`FUNNEL`), rejection metrics (`REJECTIONS`) or both (`ALL`). |

### 💬 Example Prompts

- *"Register this Backend Developer posting at Acme: [description]"*
- *"What am I missing for application 3?"*
- *"Prepare questions for the technical interview for application 3"*
- *"I got rejected from application 5, update it"*
- *"Which technologies keep showing up in the postings that rejected me?"*

---

## 🏗️ Architecture

```text
┌────────────────┐   SSE    ┌───────────────────────────────────────────┐
│   MCP client   │ ───────▶ │        Job Hunt Copilot (Spring Boot)      │
│ (VS Code, ...) │ ◀─────── │                                            │
└────────────────┘          │  mcp/tools  →  service  →  repository (JPA)│
                            │                   │                  │     │
                            └───────────────────┼──────────────────┼─────┘
                                                ▼                  ▼
                                       ┌─────────────────┐  ┌────────────┐
                                       │ Spring AI →     │  │ PostgreSQL │
                                       │ Gemini          │  │  (Docker)  │
                                       │ (structured)    │  └────────────┘
                                       └─────────────────┘
```

---

## 🧰 Tech Stack

| Area | Technology |
|---|---|
| **Language & framework** | Java 21, Spring Boot 4.1.1 |
| **AI & MCP** | Spring AI 2.0.1: MCP Server (WebMVC, SSE, sync) + Google GenAI |
| **Model** | Gemini (`gemini-2.5-flash-lite` by default) |
| **Persistence** | PostgreSQL 17, Spring Data JPA, Hibernate |
| **Other** | Lombok, Bean Validation, Maven Wrapper, Docker Compose |

---

## 🗄️ Data Model

| Table | Contents |
|---|---|
| `companies` | Companies, website, notes and how many months to wait before reapplying. |
| `job_applications` | Applications: role, description, URL, salary range, status and dates. |
| `skills` | Catalogue of technologies and skills, with a category. |
| `job_application_skills` | Application–skill link, marking each skill as required or nice-to-have. |
| `interview_prep_questions` | Generated questions, key answer points and difficulty (`EASY`, `MEDIUM`, `HARD`). |
| `user_skills` | Your skill profile, used for the gap analysis. |

Full details (in Spanish) in [`estructura_bd.md`](estructura_bd.md).

---

## 📁 Project Structure

```text
src/main/java/io/github/franra18/job_hunt_copilot_mcp/
├── mcp/
│   ├── config/McpConfig.java        # MCP tool registration
│   └── tools/                       # JobApplication, GapAnalysis, InterviewPrep and Analytics
├── service/                         # Business logic and Spring AI skill extraction
├── repository/                      # Spring Data JPA repositories
└── domain/
    ├── model/                       # JPA entities and enums
    └── dto/                         # Requests and responses
```

---

## 🚀 Getting Started

**Requirements:** Java 21, Docker and a Gemini API key.

1. **Environment variables.** Create a `.env` file in the project root:

   ```properties
   GEMINI_API_KEY=your_api_key_here
   GEMINI_MODEL=gemini-2.5-flash-lite
   ```

2. **Database.** Start PostgreSQL with Docker:

   ```bash
   docker compose up -d
   ```

3. **Server.** Run the application (tables are created automatically):

   ```bash
   # macOS / Linux
   ./mvnw spring-boot:run

   # Windows
   .\mvnw.cmd spring-boot:run
   ```

The SSE endpoint is then available at `http://localhost:8080/sse`.

---

## 🔌 Connecting an MCP Client

For example, in **VS Code**, create a `.vscode/mcp.json` file:

```json
{
  "servers": {
    "job-hunt-copilot": {
      "type": "sse",
      "url": "http://localhost:8080/sse"
    }
  }
}
```

With the server running, the tools show up in Copilot Chat's agent mode.

---

<p align="center">Made by <a href="https://github.com/franra18">Francisco Ramírez Cañadas</a></p>
