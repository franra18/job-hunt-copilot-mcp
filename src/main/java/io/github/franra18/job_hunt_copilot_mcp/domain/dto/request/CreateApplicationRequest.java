package io.github.franra18.job_hunt_copilot_mcp.domain.dto.request;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateApplicationRequest {
	@NotBlank
	private String companyName;
	@NotBlank
	private String roleTitle;
	private String jobDescription;
	private String jobUrl;
	private BigDecimal salaryMin;
	private BigDecimal salaryMax;
	private String salaryCurrency;
	private String additionalNotes;
	private Set<String> requiredSkills = new LinkedHashSet<>();
	private Set<String> optionalSkills = new LinkedHashSet<>();
	private Map<String, String> skillCategories = new java.util.LinkedHashMap<>();
}
