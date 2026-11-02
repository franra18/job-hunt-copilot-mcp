package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;

import java.time.LocalDate;

public record JobApplicationSummaryResponse(Long id, String companyName, String roleTitle, ApplicationStatus status,
		LocalDate appliedAt) {
}
