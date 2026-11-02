package io.github.franra18.job_hunt_copilot_mcp.mcp.tools;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.GapAnalysisResponse;
import io.github.franra18.job_hunt_copilot_mcp.service.GapAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class GapAnalysisTools {
	private final GapAnalysisService gapAnalysisService;

	@Tool(name = "configurar_competencias_perfil",
			description = "Actualiza las competencias técnicas del perfil; por defecto las añade y con "
					+ "replaceExisting=true reemplaza el perfil completo")
	public Set<String> updateProfile(
			@ToolParam(description = "Nombres de las tecnologías que dominas") Set<String> skills,
			@ToolParam(description = "Si es true, reemplaza el perfil completo; por defecto añade las competencias",
					required = false) Boolean replaceExisting) {
		return gapAnalysisService.updateProfile(skills, Boolean.TRUE.equals(replaceExisting));
	}

	@Tool(name = "obtener_analisis_carencias",
			description = "Compara las competencias obligatorias y deseables de una oferta con tu perfil: cuáles "
					+ "dominas y cuáles te faltan de cada tipo")
	public GapAnalysisResponse getGapAnalysis(
			@ToolParam(description = "Identificador positivo de la candidatura") Long applicationId) {
		return gapAnalysisService.analyze(applicationId);
	}
}
