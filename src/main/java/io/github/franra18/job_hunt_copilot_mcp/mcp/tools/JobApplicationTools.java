package io.github.franra18.job_hunt_copilot_mcp.mcp.tools;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.ai.SkillExtractionResult;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.request.CreateApplicationRequest;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.JobApplicationResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.JobApplicationSummaryResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import io.github.franra18.job_hunt_copilot_mcp.service.JobApplicationService;
import io.github.franra18.job_hunt_copilot_mcp.service.SkillExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Las tools son adaptadores finos: traducen la llamada MCP a servicios. Las validaciones viven en el DTO de entrada
 * y en los servicios/repositorios.
 */
@Component
@RequiredArgsConstructor
public class JobApplicationTools {

	private final JobApplicationService jobApplicationService;
	private final SkillExtractorService skillExtractorService;

	@Tool(name = "analizar_y_registrar_oferta",
			description = "Extrae competencias con Spring AI desde la descripción y registra la candidatura con "
					+ "estado APPLIED en un solo paso")
	public JobApplicationResponse createApplication(
			@ToolParam(description = "Objeto JSON con companyName y roleTitle obligatorios; al incluir "
					+ "jobDescription se extraen automáticamente sus competencias con Spring AI y se registran en "
					+ "un solo paso")
			CreateApplicationRequest request) {
		// La llamada al LLM ocurre aquí, antes de abrir la transacción del servicio.
		CreateApplicationRequest enriched = request.hasJobDescription()
				? request.withExtractedSkills(skillExtractorService.extract(request.jobDescription()))
				: request;
		return jobApplicationService.createApplication(enriched);
	}

	@Tool(name = "extraer_competencias_de_texto",
			description = "Extrae competencias obligatorias y deseables de una descripción de oferta para "
					+ "revisarlas antes de guardar la candidatura")
	public SkillExtractionResult extractSkills(
			@ToolParam(description = "Descripción completa de la oferta") String text) {
		return skillExtractorService.extract(text);
	}

	@Tool(name = "actualizar_estado_proceso",
			description = "Actualiza el estado de una candidatura y guarda las notas proporcionadas")
	public JobApplicationResponse updateApplicationStatus(
			@ToolParam(description = "Identificador positivo de la candidatura") Long applicationId,
			@ToolParam(description = "Nuevo estado: APPLIED, SCREENING, TECHNICAL_INTERVIEW, OFFER, REJECTED, "
					+ "WITHDRAWN") ApplicationStatus status,
			@ToolParam(description = "Notas de la entrevista o de la actualización", required = false)
			String interviewNotes) {
		return jobApplicationService.updateStatus(applicationId, status, interviewNotes);
	}

	@Tool(name = "obtener_candidatura", description = "Obtiene el detalle de una candidatura registrada")
	public JobApplicationResponse getApplication(
			@ToolParam(description = "Identificador positivo de la candidatura") Long applicationId) {
		return jobApplicationService.getApplication(applicationId);
	}

	@Tool(name = "listar_candidaturas",
			description = "Lista candidaturas resumidas; usa obtener_candidatura para consultar el detalle")
	public List<JobApplicationSummaryResponse> getApplications(
			@ToolParam(description = "Filtra por estado: APPLIED, SCREENING, TECHNICAL_INTERVIEW, OFFER, REJECTED "
					+ "o WITHDRAWN", required = false) ApplicationStatus status) {
		return jobApplicationService.getApplications(status);
	}
}
