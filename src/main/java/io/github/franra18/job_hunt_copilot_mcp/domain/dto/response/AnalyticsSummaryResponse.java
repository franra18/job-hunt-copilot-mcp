package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnalyticsSummaryResponse {
	private Map<ApplicationStatus, Long> funnel;
	private List<SkillRejectionStatsResponse> rejectionSkills;
}
