package io.github.franra18.job_hunt_copilot_mcp.service;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.GapAnalysisResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.JobApplication;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.UserSkill;
import io.github.franra18.job_hunt_copilot_mcp.repository.JobApplicationRepository;
import io.github.franra18.job_hunt_copilot_mcp.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class GapAnalysisService {
	private final JobApplicationRepository applicationRepository;
	private final UserSkillRepository userSkillRepository;

	@Transactional(readOnly = true)
	public GapAnalysisResponse analyze(Long applicationId) {
		JobApplication application = applicationRepository.getRequiredWithDetails(applicationId);
		Set<String> profile = profileKeys(userSkillRepository.findAll());
		Set<String> masteredRequired = sortedSet();
		Set<String> criticalGaps = sortedSet();
		Set<String> masteredOptional = sortedSet();
		Set<String> optionalGaps = sortedSet();
		application.getSkills().forEach(link -> {
			String name = link.getSkill().getName();
			boolean mastered = profile.contains(SkillNames.key(name));
			if (link.isRequired()) {
				(mastered ? masteredRequired : criticalGaps).add(name);
			} else {
				(mastered ? masteredOptional : optionalGaps).add(name);
			}
		});
		return new GapAnalysisResponse(applicationId, masteredRequired, criticalGaps, masteredOptional, optionalGaps);
	}

	@Transactional
	public Set<String> updateProfile(Set<String> names, boolean replaceExisting) {
		if (replaceExisting) {
			userSkillRepository.deleteAllInBatch();
		}
		Set<String> existing = profileKeys(userSkillRepository.findAll());
		List<UserSkill> toCreate = SkillNames.clean(names).entrySet().stream()
				.filter(entry -> !existing.contains(entry.getKey()))
				.map(entry -> {
					UserSkill skill = new UserSkill();
					skill.setName(entry.getValue());
					return skill;
				})
				.toList();
		userSkillRepository.saveAll(toCreate);
		Set<String> profile = new LinkedHashSet<>();
		userSkillRepository.findAllByOrderByNameAsc().forEach(skill -> profile.add(skill.getName()));
		return profile;
	}

	private static Set<String> profileKeys(Collection<UserSkill> skills) {
		return SkillNames.clean(skills.stream().map(UserSkill::getName).toList()).keySet();
	}

	private static Set<String> sortedSet() {
		return new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	}
}
