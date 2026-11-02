package io.github.franra18.job_hunt_copilot_mcp.domain.dto.ai;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Esquema de salida estructurada que se le pide al LLM al extraer competencias de una oferta.
 */
public record SkillExtractionResult(
		Set<String> requiredSkills,
		Set<String> optionalSkills,
		Map<String, String> skillCategories) {

	public SkillExtractionResult {
		requiredSkills = requiredSkills == null ? new LinkedHashSet<>() : new LinkedHashSet<>(requiredSkills);
		optionalSkills = optionalSkills == null ? new LinkedHashSet<>() : new LinkedHashSet<>(optionalSkills);
		skillCategories = skillCategories == null ? new LinkedHashMap<>() : new LinkedHashMap<>(skillCategories);
	}
}
