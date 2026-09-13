package io.github.franra18.job_hunt_copilot_mcp.domain.dto.request;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
	private ApplicationStatus status;
	private String interviewNotes;
}
