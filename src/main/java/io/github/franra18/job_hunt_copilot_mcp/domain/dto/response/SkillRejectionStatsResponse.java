package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

/**
 * También se usa como proyección directa en JobApplicationRepository (select new ...).
 */
public record SkillRejectionStatsResponse(String skillName, Long rejectedApplications) {
}
