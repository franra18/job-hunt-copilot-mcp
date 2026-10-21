# 🧭 Job Hunt Copilot MCP

> **Un copiloto para tu búsqueda de empleo: registra candidaturas, detecta qué te falta para cada oferta y prepara tus entrevistas técnicas, todo desde tu cliente de IA.**

**Job Hunt Copilot** es un servidor **MCP (Model Context Protocol)** construido con **Spring Boot**, **Spring AI** y **Gemini**. Conéctalo a cualquier cliente compatible con MCP (VS Code, Claude Desktop...) y gestiona tu búsqueda de empleo hablando en lenguaje natural: pega una oferta y el servidor extrae sus competencias, la guarda en PostgreSQL y te ayuda a seguirla hasta el final.

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring_AI_2-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Gemini](https://img.shields.io/badge/Gemini-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL_17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
[![Serie en el blog](https://img.shields.io/badge/📝_Serie_en_el_blog-franra18.github.io-2ea44f?style=for-the-badge)](https://franra18.github.io/blog/projects/job-hunt-mcp/)

---

## 📑 Índice

- [Vídeo](#-vídeo)
- [Funcionalidades](#-funcionalidades)
- [Herramientas MCP](#️-herramientas-mcp)
- [Arquitectura](#️-arquitectura)
- [Stack tecnológico](#-stack-tecnológico)
- [Modelo de datos](#️-modelo-de-datos)
- [Estructura del proyecto](#-estructura-del-proyecto)
- [Cómo ejecutarlo](#-cómo-ejecutarlo)
- [Conectarlo a un cliente MCP](#-conectarlo-a-un-cliente-mcp)

---

## 🎥 Vídeo


https://github.com/user-attachments/assets/65772638-d556-43ba-9f53-92cb965e1555


---

## ✨ Funcionalidades

- **📝 Registro inteligente de ofertas:** pega la descripción de una oferta y Gemini extrae las competencias **obligatorias** y **deseables** con salida estructurada antes de guardar la candidatura.
- **🔄 Seguimiento del proceso:** estados `APPLIED` → `SCREENING` → `TECHNICAL_INTERVIEW` → `OFFER` / `REJECTED` / `WITHDRAWN`, con notas de cada entrevista.
- **🎯 Análisis de carencias:** compara los requisitos de cada oferta con tu perfil técnico y te dice qué dominas, qué te falta y qué deseables cumples.
- **🎤 Preparación de entrevistas:** genera preguntas técnicas difíciles adaptadas al puesto, con puntos clave de respuesta y nivel de dificultad, y las guarda para repasarlas.
- **📊 Métricas:** embudo de candidaturas por estado y qué tecnologías se repiten más en las ofertas en las que te rechazan.

---

## 🛠️ Herramientas MCP

| Herramienta | Qué hace |
|---|---|
| `analizar_y_registrar_oferta` | Extrae las competencias de la oferta con IA y registra la candidatura (estado `APPLIED`) en un solo paso. |
| `extraer_competencias_de_texto` | Analiza las competencias de una oferta sin guardar nada, para revisarlas antes. |
| `actualizar_estado_proceso` | Cambia el estado de una candidatura y guarda notas de la entrevista. |
| `obtener_candidatura` | Devuelve el detalle completo de una candidatura. |
| `listar_candidaturas` | Lista las candidaturas resumidas, con filtro opcional por estado. |
| `configurar_competencias_perfil` | Añade competencias a tu perfil técnico (o lo reemplaza con `replaceExisting=true`). |
| `obtener_analisis_carencias` | Compara la oferta con tu perfil: competencias dominadas, carencias y deseables que cumples. |
| `generar_preguntas_entrevista` | Genera y guarda preguntas técnicas basadas en el puesto y sus competencias. |
| `obtener_preparacion_entrevista` | Recupera las preguntas guardadas de una candidatura. |
| `obtener_resumen_metricas` | Métricas del embudo (`FUNNEL`), de rechazos (`REJECTIONS`) o ambas (`ALL`). |

### 💬 Ejemplos de uso

- *"Registra esta oferta de Backend Developer en Acme: [descripción]"*
- *"¿Qué me falta para la candidatura 3?"*
- *"Prepárame preguntas para la entrevista técnica de la candidatura 3"*
- *"Me han rechazado en la 5, actualízala"*
- *"¿Qué tecnologías se repiten en las ofertas en las que me rechazan?"*

---

## 🏗️ Arquitectura

```text
┌────────────────┐   SSE    ┌───────────────────────────────────────────┐
│  Cliente MCP   │ ───────▶ │        Job Hunt Copilot (Spring Boot)      │
│ (VS Code, ...) │ ◀─────── │                                            │
└────────────────┘          │  mcp/tools  →  service  →  repository (JPA)│
                            │                   │                  │     │
                            └───────────────────┼──────────────────┼─────┘
                                                ▼                  ▼
                                       ┌─────────────────┐  ┌────────────┐
                                       │ Spring AI →     │  │ PostgreSQL │
                                       │ Gemini (JSON    │  │   (Docker) │
                                       │ estructurado)   │  └────────────┘
                                       └─────────────────┘
```

---

## 🧰 Stack tecnológico

| Área | Tecnología |
|---|---|
| **Lenguaje y framework** | Java 21, Spring Boot 4.1.1 |
| **IA y MCP** | Spring AI 2.0.1: MCP Server (WebMVC, SSE, síncrono) + Google GenAI |
| **Modelo** | Gemini (`gemini-2.5-flash-lite` por defecto) |
| **Persistencia** | PostgreSQL 17, Spring Data JPA, Hibernate |
| **Otros** | Lombok, Bean Validation, Maven Wrapper, Docker Compose |

---

## 🗄️ Modelo de datos

| Tabla | Contenido |
|---|---|
| `companies` | Empresas, web, notas y meses de espera para volver a aplicar. |
| `job_applications` | Candidaturas: puesto, descripción, URL, rango salarial, estado y fechas. |
| `skills` | Catálogo de tecnologías y competencias, con categoría. |
| `job_application_skills` | Relación candidatura–competencia, indicando si es obligatoria o deseable. |
| `interview_prep_questions` | Preguntas generadas, puntos clave de respuesta y dificultad (`EASY`, `MEDIUM`, `HARD`). |
| `user_skills` | Tu perfil técnico, que se usa para el análisis de carencias. |

El detalle completo está en [`estructura_bd.md`](estructura_bd.md).

---

## 📁 Estructura del proyecto

```text
src/main/java/io/github/franra18/job_hunt_copilot_mcp/
├── mcp/
│   ├── config/McpConfig.java        # Registro de herramientas MCP
│   └── tools/                       # JobApplication, GapAnalysis, InterviewPrep y Analytics
├── service/                         # Lógica de negocio y extracción de competencias con Spring AI
├── repository/                      # Repositorios Spring Data JPA
└── domain/
    ├── model/                       # Entidades JPA y enums
    └── dto/                         # Peticiones y respuestas
```

---

## 🚀 Cómo ejecutarlo

**Requisitos:** Java 21, Docker y una API key de Gemini.

1. **Variables de entorno.** Crea un archivo `.env` en la raíz:

   ```properties
   GEMINI_API_KEY=tu_api_key_aqui
   GEMINI_MODEL=gemini-2.5-flash-lite
   ```

2. **Base de datos.** Levanta PostgreSQL con Docker:

   ```bash
   docker compose up -d
   ```

3. **Servidor.** Arranca la aplicación (las tablas se crean automáticamente):

   ```bash
   # macOS / Linux
   ./mvnw spring-boot:run

   # Windows
   .\mvnw.cmd spring-boot:run
   ```

El endpoint SSE queda disponible en `http://localhost:8080/sse`.

---

## 🔌 Conectarlo a un cliente MCP

Por ejemplo, en **VS Code**, crea un archivo `.vscode/mcp.json`:

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

Con el servidor en marcha, las herramientas aparecerán en el modo agente de Copilot Chat.

---

<p align="center">Hecho por <a href="https://github.com/franra18">Francisco Ramírez Cañadas</a></p>
