package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public record SkillExtractionResponse(
		Set<String> requiredSkills,
		Set<String> optionalSkills,
		Map<String, String> skillCategories) {
	public SkillExtractionResponse {
		requiredSkills = requiredSkills == null ? new LinkedHashSet<>() : new LinkedHashSet<>(requiredSkills);
		optionalSkills = optionalSkills == null ? new LinkedHashSet<>() : new LinkedHashSet<>(optionalSkills);
		skillCategories = skillCategories == null ? new LinkedHashMap<>() : new LinkedHashMap<>(skillCategories);
	}
}
