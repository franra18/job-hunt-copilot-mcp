package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record JobApplicationResponse(
		Long id,
		String companyName,
		String roleTitle,
		String jobDescription,
		String jobUrl,
		BigDecimal salaryMin,
		BigDecimal salaryMax,
		String salaryCurrency,
		ApplicationStatus status,
		LocalDate appliedAt,
		String additionalNotes,
		Set<String> requiredSkills,
		Set<String> optionalSkills) {
}
