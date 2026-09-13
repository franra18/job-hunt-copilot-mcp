package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import java.util.Set;

public record GapAnalysisResponse(Long applicationId, Set<String> masteredRequiredSkills,
		Set<String> criticalGaps, Set<String> desirableSkills) {
}
