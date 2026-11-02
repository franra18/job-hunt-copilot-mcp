package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;

import java.util.List;
import java.util.Map;

public record AnalyticsSummaryResponse(Map<ApplicationStatus, Long> funnel,
		List<SkillRejectionStatsResponse> rejectionSkills) {
}
