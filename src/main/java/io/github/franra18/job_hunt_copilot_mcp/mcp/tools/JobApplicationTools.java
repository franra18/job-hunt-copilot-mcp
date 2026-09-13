package io.github.franra18.job_hunt_copilot_mcp.mcp.tools;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.request.CreateApplicationRequest;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.JobApplicationResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.JobApplicationSummaryResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.SkillExtractionResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import io.github.franra18.job_hunt_copilot_mcp.service.JobApplicationService;
import io.github.franra18.job_hunt_copilot_mcp.service.SkillExtractorService;
import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class JobApplicationTools {

	private final JobApplicationService jobApplicationService;
	private final SkillExtractorService skillExtractorService;

	@Tool(name = "analizar_y_registrar_oferta",
			description = "Extrae competencias con Spring AI desde la descripción y registra la candidatura con estado APPLIED en un solo paso")
	public JobApplicationResponse createApplication(
			@ToolParam(description = "Objeto JSON con companyName y roleTitle obligatorios; al incluir jobDescription se extraen automáticamente sus competencias con Spring AI y se registran en un solo paso")
			@Valid CreateApplicationRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("request es obligatorio");
		}
		if (request.getJobDescription() != null && !request.getJobDescription().isBlank()) {
			SkillExtractionResponse extracted = skillExtractorService.extract(request.getJobDescription());
			request.setRequiredSkills(merge(request.getRequiredSkills(), extracted.requiredSkills()));
			request.setOptionalSkills(merge(request.getOptionalSkills(), extracted.optionalSkills()));
			request.setSkillCategories(mergeCategories(request.getSkillCategories(), extracted.skillCategories()));
		}
		return jobApplicationService.createApplication(request);
	}

	@Tool(name = "extraer_competencias_de_texto",
			description = "Extrae competencias obligatorias y deseables de una descripción de oferta para revisarlas antes de guardar la candidatura")
	public SkillExtractionResponse extractSkills(
			@ToolParam(description = "Descripción completa de la oferta") String text) {
		return skillExtractorService.extract(text);
	}

	@Tool(name = "actualizar_estado_proceso",
			description = "Actualiza el estado de una candidatura y guarda las notas proporcionadas")
	public JobApplicationResponse updateApplicationStatus(
			@ToolParam(description = "Identificador de la candidatura") Long applicationId,
			@ToolParam(description = "Nuevo estado: APPLIED, SCREENING, TECHNICAL_INTERVIEW, OFFER, REJECTED, WITHDRAWN") ApplicationStatus status,
			@ToolParam(description = "Notas de la entrevista o de la actualización", required = false) String interviewNotes) {
		requireApplicationId(applicationId);
		if (status == null) {
			throw new IllegalArgumentException("status es obligatorio");
		}
		return jobApplicationService.updateStatus(applicationId, status, interviewNotes);
	}

	@Tool(name = "obtener_candidatura",
			description = "Obtiene el detalle de una candidatura registrada")
	public JobApplicationResponse getApplication(
			@ToolParam(description = "Identificador de la candidatura") Long applicationId) {
		requireApplicationId(applicationId);
		return jobApplicationService.getApplication(applicationId);
	}

	@Tool(name = "listar_candidaturas",
			description = "Lista candidaturas resumidas; usa obtener_candidatura para consultar el detalle")
	public List<JobApplicationSummaryResponse> getApplications(
			@ToolParam(description = "Filtra por estado: APPLIED, SCREENING, TECHNICAL_INTERVIEW, OFFER, REJECTED o WITHDRAWN", required = false)
			ApplicationStatus status) {
		return jobApplicationService.getApplications(status);
	}

	private void requireApplicationId(Long applicationId) {
		if (applicationId == null || applicationId <= 0) {
			throw new IllegalArgumentException("applicationId debe ser un número positivo");
		}
	}

	private java.util.Set<String> merge(java.util.Set<String> current, java.util.Set<String> extracted) {
		java.util.Set<String> merged = new java.util.LinkedHashSet<>();
		if (current != null) merged.addAll(current);
		if (extracted != null) merged.addAll(extracted);
		return merged;
	}

	private java.util.Map<String, String> mergeCategories(java.util.Map<String, String> current,
			java.util.Map<String, String> extracted) {
		java.util.Map<String, String> merged = new java.util.LinkedHashMap<>();
		if (extracted != null) merged.putAll(extracted);
		if (current != null) merged.putAll(current);
		return merged;
	}
}
