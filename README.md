# Job Hunt Copilot MCP

Servidor MCP para gestionar candidaturas, extraer competencias de ofertas y preparar entrevistas técnicas con Spring AI y Gemini.

El proyecto está pensado como un MVP de portfolio: mantiene la persistencia sencilla y concentra el valor en la integración entre Spring AI, PostgreSQL y el Model Context Protocol.

## Tecnologías

- Java 21
- Spring Boot 4.1.1
- Spring AI 2.0.1
- Spring AI MCP Server WebMVC con transporte SSE
- Spring AI Google GenAI para Gemini
- Spring Data JPA e Hibernate
- PostgreSQL 17
- Maven
- Docker Compose

## Funcionalidades

- Registrar una candidatura y extraer automáticamente sus competencias desde la descripción de la oferta.
- Revisar competencias sin guardar la candidatura mediante `extraer_competencias_de_texto`.
- Actualizar el estado del proceso: `APPLIED`, `SCREENING`, `TECHNICAL_INTERVIEW`, `OFFER`, `REJECTED` o `WITHDRAWN`.
- Consultar una candidatura concreta o listar candidaturas por estado.
- Generar preguntas técnicas personalizadas para una candidatura.
- Mantener un perfil de competencias técnicas.
- Comparar las competencias de una oferta con el perfil y detectar carencias.
- Consultar el embudo de candidaturas y las competencias más frecuentes en candidaturas rechazadas.
- Exponer todas estas capacidades como herramientas MCP para clientes como Claude Desktop, Cursor u otros clientes compatibles.

## Proceso

1. El cliente MCP envía una petición en lenguaje natural.
2. Spring AI registra la petición como una llamada a una herramienta MCP.
3. Para registrar una candidatura con descripción, el servidor envía el texto a Gemini mediante `ChatClient`.
4. Gemini devuelve una respuesta estructurada con competencias obligatorias, deseables y categorías.
5. Spring Data JPA guarda la empresa, la candidatura, las competencias normalizadas y sus relaciones.
6. Otras herramientas consultan la información persistida para preparar entrevistas, analizar carencias y obtener métricas.

El flujo principal se puede resumir así:

```text
Cliente MCP -> herramienta MCP -> servicio Spring -> ChatClient/Gemini
                                      |
                                      v
                                  PostgreSQL
```

## Cómo ejecutar el proyecto

### Requisitos

- Java 21
- Docker y Docker Compose
- Una API key de Google Gemini

### 1. Configurar Gemini

Crea un archivo `.env` en la raíz del proyecto:

```properties
GEMINI_API_KEY=tu_api_key_de_gemini
GEMINI_MODEL=gemini-2.5-flash-lite
```

No subas este archivo al repositorio. Ya está incluido en `.gitignore`.

### 2. Iniciar PostgreSQL

```bash
docker compose up -d
```

Esto inicia PostgreSQL en `localhost:5432` con la base de datos `job_hunt_copilot`.

### 3. Ejecutar la aplicación

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En macOS o Linux:

```bash
./mvnw spring-boot:run
```

El servidor MCP quedará disponible mediante SSE en:

```text
http://localhost:8080/sse
```

También se incluye una configuración para VS Code en `.vscode/mcp.json`.

### 4. Construir el proyecto

```bash
./mvnw package
```

En Windows PowerShell:

```powershell
.\mvnw.cmd package
```

Para construir sin ejecutar las pruebas:

```powershell
.\mvnw.cmd package -DskipTests
```

### 5. Detener PostgreSQL

```bash
docker compose down
```

Para eliminar también los datos persistidos del volumen:

```bash
docker compose down -v
```

## Herramientas MCP principales

| Herramienta | Propósito |
| --- | --- |
| `analizar_y_registrar_oferta` | Extrae competencias y registra una candidatura |
| `extraer_competencias_de_texto` | Analiza competencias sin guardar |
| `actualizar_estado_proceso` | Actualiza el estado de una candidatura |
| `obtener_candidatura` | Consulta el detalle de una candidatura |
| `listar_candidaturas` | Lista candidaturas por estado |
| `generar_preguntas_entrevista` | Genera preguntas técnicas con Gemini |
| `obtener_preparacion_entrevista` | Consulta preguntas guardadas |
| `obtener_resumen_metricas` | Consulta métricas y estadísticas |
| `configurar_competencias_perfil` | Actualiza el perfil técnico |
| `obtener_analisis_carencias` | Compara una oferta con el perfil |

## Vídeo mostrando el proyecto

Vídeo de demostración en directo: **pendiente de publicar**.

Cuando esté disponible, se añadirá aquí el enlace al vídeo mostrando la conexión desde un cliente MCP, el registro de una candidatura, la extracción de competencias y la generación de preguntas de entrevista.

## Documentación adicional

- [Estructura de la base de datos](estructura_bd.md)
