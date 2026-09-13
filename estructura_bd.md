**1. `companies` (Empresas)**

* `id` (PK, BIGINT autogenerado)
* `name` (VARCHAR, UNIQUE)
* `website` (VARCHAR)
* `reapply_cooldown_months` (INT, nullable — ej. 12 meses)
* `notes` (TEXT)

---

**2. `job_applications` (Candidaturas)**
Centraliza la oferta y el estado actual del proceso.

* `id` (PK, BIGINT autogenerado)
* `company_id` (FK -> `companies.id`)
* `role_title` (VARCHAR)
* `job_description` (TEXT — texto completo de la oferta)
* `job_url` (VARCHAR)
* `salary_min` (DECIMAL/INT, nullable)
* `salary_max` (DECIMAL/INT, nullable)
* `salary_currency` (VARCHAR, ej. 'EUR', 'USD')
* `status` (ENUM almacenado como texto: `APPLIED`, `SCREENING`, `TECHNICAL_INTERVIEW`, `OFFER`, `REJECTED`, `WITHDRAWN`)
* `applied_at` (DATE)
* `additional_notes` (TEXT)
* `created_at` / `updated_at` (TIMESTAMP)

---

**3. `skills` (Catálogo de Tecnologías/Habilidades)**
Permite agregar datos para consultas como *"qué tecnologías se repiten más en las que me rechazan"*.

* `id` (PK, BIGINT autogenerado)
* `name` (VARCHAR, UNIQUE — ej. "Docker", "Kubernetes", "Rust", "Python")
* `category` (VARCHAR, ej. "language", "devops", "cloud", "database")

---

**4. `job_application_skills` (Relación Candidatura-Competencia)**
Tabla pivote que el MCP rellena al extraer los requisitos de la descripción.

* `application_id` (FK -> `job_applications.id`, NOT NULL)
* `skill_id` (FK -> `skills.id`, NOT NULL)
* `is_required` (BOOLEAN — `true` para requisitos obligatorios, `false` para deseables)
* `id` (PK, BIGINT autogenerado)
* Restricción UNIQUE sobre (`application_id`, `skill_id`) para evitar duplicar una competencia en la misma candidatura.

---

**5. `interview_prep_questions` (Banco de Preguntas para Estudio)**
Guarda las preguntas técnicas generadas por el MCP para repasar antes de una entrevista.

* `id` (PK, BIGINT autogenerado)
* `application_id` (FK -> `job_applications.id`, nullable)
* `skill_id` (FK -> `skills.id`, nullable)
* `question` (TEXT)
* `suggested_answer_points` (TEXT)
* `difficulty` (ENUM almacenado como texto: `EASY`, `MEDIUM`, `HARD`)

---

**6. `user_skills` (Competencias del perfil del usuario)**
Mantiene las tecnologías que el usuario declara dominar para el análisis de carencias.

* `id` (PK, BIGINT autogenerado)
* `name` (VARCHAR, UNIQUE)
