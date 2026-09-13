package io.github.franra18.job_hunt_copilot_mcp.service;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.GapAnalysisResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.JobApplication;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.UserSkill;
import io.github.franra18.job_hunt_copilot_mcp.repository.JobApplicationRepository;
import io.github.franra18.job_hunt_copilot_mcp.repository.UserSkillRepository;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GapAnalysisService {
	private final JobApplicationRepository applicationRepository;
	private final UserSkillRepository userSkillRepository;

	@Transactional(readOnly = true)
	public GapAnalysisResponse analyze(Long applicationId) {
		JobApplication application = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new IllegalArgumentException("No se encontró la candidatura: " + applicationId));
		Set<String> profile = userSkillRepository.findAllByOrderByNameAsc().stream()
				.map(UserSkill::getName).map(name -> name.toLowerCase(Locale.ROOT)).collect(java.util.stream.Collectors.toSet());
		Set<String> mastered = new LinkedHashSet<>();
		Set<String> gaps = new LinkedHashSet<>();
		Set<String> desirable = new LinkedHashSet<>();
		application.getSkills().forEach(link -> {
			String name = link.getSkill().getName();
			if (link.isRequired()) {
				if (profile.contains(name.toLowerCase(Locale.ROOT))) mastered.add(name);
				else gaps.add(name);
			} else if (profile.contains(name.toLowerCase(Locale.ROOT))) {
				desirable.add(name);
			}
		});
		return new GapAnalysisResponse(applicationId, mastered, gaps, desirable);
	}

	@Transactional
	public Set<String> updateProfile(Set<String> names, boolean replaceExisting) {
		if (replaceExisting) {
			userSkillRepository.deleteAllInBatch();
		}
		Set<String> normalized = new LinkedHashSet<>();
		if (names != null) names.stream().filter(name -> name != null && !name.isBlank())
				.map(String::trim).forEach(normalized::add);
		Set<String> existing = userSkillRepository.findAllByOrderByNameAsc().stream()
				.map(UserSkill::getName)
				.collect(java.util.stream.Collectors.toSet());
		userSkillRepository.saveAll(normalized.stream()
				.filter(name -> existing.stream().noneMatch(value -> value.equalsIgnoreCase(name)))
				.map(name -> {
			UserSkill skill = new UserSkill();
			skill.setName(name);
			return skill;
		}).toList());
		return userSkillRepository.findAllByOrderByNameAsc().stream().map(UserSkill::getName)
				.collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
	}

}
