package io.github.franra18.job_hunt_copilot_mcp.mcp.tools;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.InterviewPrepResponse;
import io.github.franra18.job_hunt_copilot_mcp.service.InterviewPrepService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewPrepTools {

	private final InterviewPrepService interviewPrepService;

	@Tool(name = "generar_preguntas_entrevista",
			description = "Genera y guarda preguntas técnicas difíciles usando el puesto y las competencias de la oferta")
	public InterviewPrepResponse generateInterviewQuestions(
			@ToolParam(description = "Identificador positivo de la candidatura cuya oferta se usará como contexto") Long applicationId) {
		return interviewPrepService.generateQuestions(requireApplicationId(applicationId));
	}

	@Tool(name = "obtener_preparacion_entrevista",
			description = "Obtiene las preguntas técnicas guardadas para una candidatura")
	public InterviewPrepResponse getInterviewPreparation(
			@ToolParam(description = "Identificador de la candidatura") Long applicationId) {
		return interviewPrepService.getPreparation(requireApplicationId(applicationId));
	}

	private Long requireApplicationId(Long applicationId) {
		if (applicationId == null || applicationId <= 0) {
			throw new IllegalArgumentException("applicationId debe ser un número positivo");
		}
		return applicationId;
	}
}
