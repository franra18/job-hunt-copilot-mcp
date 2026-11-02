package io.github.franra18.job_hunt_copilot_mcp.domain.dto.request;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.ai.SkillExtractionResult;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Entrada de la tool analizar_y_registrar_oferta. La validación vive en el constructor compacto, así se aplica
 * siempre, venga la petición del cliente MCP o de código interno.
 */
public record CreateApplicationRequest(
		String companyName,
		String roleTitle,
		String jobDescription,
		String jobUrl,
		BigDecimal salaryMin,
		BigDecimal salaryMax,
		String salaryCurrency,
		String additionalNotes,
		Set<String> requiredSkills,
		Set<String> optionalSkills,
		Map<String, String> skillCategories) {

	public CreateApplicationRequest {
		companyName = requireText(companyName, "companyName");
		roleTitle = requireText(roleTitle, "roleTitle");
		requiredSkills = requiredSkills == null ? new LinkedHashSet<>() : new LinkedHashSet<>(requiredSkills);
		optionalSkills = optionalSkills == null ? new LinkedHashSet<>() : new LinkedHashSet<>(optionalSkills);
		skillCategories = skillCategories == null ? new LinkedHashMap<>() : new LinkedHashMap<>(skillCategories);
	}

	public boolean hasJobDescription() {
		return jobDescription != null && !jobDescription.isBlank();
	}

	/**
	 * Devuelve una copia con las competencias extraídas por el LLM añadidas a las que ya traía la petición.
	 * Si una categoría viene en ambos sitios, gana la de la petición.
	 */
	public CreateApplicationRequest withExtractedSkills(SkillExtractionResult extracted) {
		Set<String> required = new LinkedHashSet<>(requiredSkills);
		required.addAll(extracted.requiredSkills());
		Set<String> optional = new LinkedHashSet<>(optionalSkills);
		optional.addAll(extracted.optionalSkills());
		Map<String, String> categories = new LinkedHashMap<>(extracted.skillCategories());
		categories.putAll(skillCategories);
		return new CreateApplicationRequest(companyName, roleTitle, jobDescription, jobUrl, salaryMin, salaryMax,
				salaryCurrency, additionalNotes, required, optional, categories);
	}

	private static String requireText(String value, String field) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(field + " es obligatorio");
		}
		return value.trim();
	}
}
