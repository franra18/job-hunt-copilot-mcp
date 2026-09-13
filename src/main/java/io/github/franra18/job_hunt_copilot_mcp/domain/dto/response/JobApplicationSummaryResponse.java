package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobApplicationSummaryResponse {
	private Long id;
	private String companyName;
	private String roleTitle;
	private ApplicationStatus status;
	private LocalDate appliedAt;
}
