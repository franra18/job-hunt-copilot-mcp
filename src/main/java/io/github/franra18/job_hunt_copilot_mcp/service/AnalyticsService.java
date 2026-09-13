package io.github.franra18.job_hunt_copilot_mcp.service;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.AnalyticsSummaryResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.SkillRejectionStatsResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.AnalyticsMetricType;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import io.github.franra18.job_hunt_copilot_mcp.repository.JobApplicationRepository;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
	private final JobApplicationRepository applicationRepository;

	@Transactional(readOnly = true)
	public AnalyticsSummaryResponse getSummary(AnalyticsMetricType type) {
		AnalyticsMetricType requestedType = type == null ? AnalyticsMetricType.ALL : type;
		Map<ApplicationStatus, Long> funnel = requestedType == AnalyticsMetricType.REJECTIONS ? null : getFunnel();
		List<SkillRejectionStatsResponse> rejectionSkills = requestedType == AnalyticsMetricType.FUNNEL ? null
				: getRejectedSkillStats();
		return new AnalyticsSummaryResponse(funnel, rejectionSkills);
	}

	@Transactional(readOnly = true)
	public Map<ApplicationStatus, Long> getFunnel() {
		Map<ApplicationStatus, Long> funnel = new EnumMap<>(ApplicationStatus.class);
		Arrays.stream(ApplicationStatus.values()).forEach(status -> funnel.put(status, 0L));
		applicationRepository.countByStatus().forEach(row -> funnel.put((ApplicationStatus) row[0], (Long) row[1]));
		return funnel;
	}

	@Transactional(readOnly = true)
	public List<SkillRejectionStatsResponse> getRejectedSkillStats() {
		return applicationRepository.countRejectedApplicationsBySkill(ApplicationStatus.REJECTED).stream()
			.map(row -> new SkillRejectionStatsResponse((String) row[0], (Long) row[1]))
				.toList();
	}
}
