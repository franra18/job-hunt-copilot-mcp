package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillRejectionStatsResponse {
	private String skillName;
	private long rejectedApplications;
}
