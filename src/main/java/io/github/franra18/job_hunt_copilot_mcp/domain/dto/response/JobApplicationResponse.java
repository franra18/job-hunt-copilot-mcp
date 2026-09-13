package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobApplicationResponse {
	private Long id;
	private String companyName;
	private String roleTitle;
	private String jobDescription;
	private String jobUrl;
	private BigDecimal salaryMin;
	private BigDecimal salaryMax;
	private String salaryCurrency;
	private ApplicationStatus status;
	private LocalDate appliedAt;
	private String additionalNotes;
	private Set<String> requiredSkills;
	private Set<String> optionalSkills;
}
