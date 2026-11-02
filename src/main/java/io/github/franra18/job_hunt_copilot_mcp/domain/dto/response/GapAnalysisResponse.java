package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import java.util.Set;

/**
 * Resultado del análisis de carencias de una candidatura frente al perfil del usuario.
 *
 * @param masteredRequiredSkills obligatorias que ya dominas
 * @param criticalGaps           obligatorias que te faltan
 * @param masteredOptionalSkills deseables que ya dominas
 * @param optionalGaps           deseables que te faltan
 */
public record GapAnalysisResponse(Long applicationId, Set<String> masteredRequiredSkills, Set<String> criticalGaps,
		Set<String> masteredOptionalSkills, Set<String> optionalGaps) {
}
