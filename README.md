# Job Hunt Copilot MCP

Servidor MCP construido con Spring AI y Gemini para automatizar el seguimiento de candidaturas, analizar ofertas laborales y preparar entrevistas técnicas.

## Stack tecnológico

- **Java 21** & **Spring Boot 4.1.1**
- **Spring AI 2.0.1** (MCP Server SSE + Google GenAI)
- **PostgreSQL 17** & Spring Data JPA
- **Docker Compose** & Maven

## Arquitectura

```text
Cliente MCP -> Herramienta MCP -> Spring AI -> Gemini (Extracción estructurada)
                                       |
                                       v
                                   PostgreSQL
```

## Herramientas MCP disponibles

| Herramienta | Acción |
| --- | --- |
| `analizar_y_registrar_oferta` | Extrae competencias de la descripción y guarda la candidatura. |
| `extraer_competencias_de_texto` | Analiza competencias sin persistir datos. |
| `actualizar_estado_proceso` | Modifica el estado (`APPLIED`, `INTERVIEW`, `OFFER`, etc.). |
| `obtener_candidatura` / `listar_candidaturas` | Consulta detalles o filtra por estado. |
| `generar_preguntas_entrevista` | Genera preguntas técnicas adaptadas al rol. |
| `configurar_competencias_perfil` | Actualiza tu perfil técnico base. |
| `obtener_analisis_carencias` | Detecta gaps entre la oferta y tu perfil. |
| `obtener_resumen_metricas` | Estadísticas del embudo y motivos de rechazo. |

---

## Ejecutar el proyecto

### 1. Variables de entorno

Crea un archivo `.env` en la raíz:

```properties
GEMINI_API_KEY=tu_api_key_aqui
GEMINI_MODEL=gemini-2.5-flash-lite
```

### 2. Iniciar base de datos

```bash
docker compose up -d
```

### 3. Ejecutar el servidor

```bash
# macOS / Linux
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

El endpoint SSE quedará expuesto en: `http://localhost:8080/sse`

*(Configuración para VS Code disponible en `.vscode/mcp.json`)*.

---

## Recursos adicionales

* [Estructura de la base de datos](estructura_bd.md)

## Vídeo


https://github.com/user-attachments/assets/65772638-d556-43ba-9f53-92cb965e1555

