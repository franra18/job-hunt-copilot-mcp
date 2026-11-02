package io.github.franra18.job_hunt_copilot_mcp.service;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.AnalyticsSummaryResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import io.github.franra18.job_hunt_copilot_mcp.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
	private final JobApplicationRepository applicationRepository;

	@Transactional(readOnly = true)
	public AnalyticsSummaryResponse getSummary() {
		Map<ApplicationStatus, Long> funnel = new EnumMap<>(ApplicationStatus.class);
		for (ApplicationStatus status : ApplicationStatus.values()) {
			funnel.put(status, 0L);
		}
		applicationRepository.countByStatus().forEach(row -> funnel.put(row.status(), row.total()));
		return new AnalyticsSummaryResponse(funnel,
				applicationRepository.countApplicationsBySkill(ApplicationStatus.REJECTED));
	}
}
