package io.github.franra18.job_hunt_copilot_mcp.service;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.request.CreateApplicationRequest;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.JobApplicationResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.JobApplicationSummaryResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.Company;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.JobApplication;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.Skill;
import io.github.franra18.job_hunt_copilot_mcp.repository.CompanyRepository;
import io.github.franra18.job_hunt_copilot_mcp.repository.JobApplicationRepository;
import io.github.franra18.job_hunt_copilot_mcp.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JobApplicationService {
	private final JobApplicationRepository applicationRepository;
	private final CompanyRepository companyRepository;
	private final SkillRepository skillRepository;

	@Transactional
	public JobApplicationResponse createApplication(CreateApplicationRequest request) {
		JobApplication application = new JobApplication();
		application.setCompany(findOrCreateCompany(request.companyName()));
		application.setRoleTitle(request.roleTitle());
		application.setJobDescription(request.jobDescription());
		application.setJobUrl(request.jobUrl());
		application.setSalaryMin(request.salaryMin());
		application.setSalaryMax(request.salaryMax());
		application.setSalaryCurrency(request.salaryCurrency());
		application.setAdditionalNotes(request.additionalNotes());
		application.setStatus(ApplicationStatus.APPLIED);
		application.setAppliedAt(LocalDate.now());

		Map<String, String> required = SkillNames.clean(request.requiredSkills());
		Map<String, String> optional = SkillNames.clean(request.optionalSkills());
		optional.keySet().removeAll(required.keySet());
		Map<String, String> all = new LinkedHashMap<>(required);
		all.putAll(optional);
		Map<String, Skill> skills = findOrCreateSkills(all, normalizeCategories(request.skillCategories()));
		required.keySet().forEach(key -> application.addSkill(skills.get(key), true));
		optional.keySet().forEach(key -> application.addSkill(skills.get(key), false));

		return toResponse(applicationRepository.save(application));
	}

	@Transactional
	public JobApplicationResponse updateStatus(Long applicationId, ApplicationStatus status, String interviewNotes) {
		if (status == null) {
			throw new IllegalArgumentException("status es obligatorio");
		}
		JobApplication application = applicationRepository.getRequiredWithDetails(applicationId);
		application.setStatus(status);
		if (interviewNotes != null && !interviewNotes.isBlank()) {
			application.setAdditionalNotes(appendNote(application.getAdditionalNotes(), interviewNotes));
		}
		return toResponse(application);
	}

	@Transactional(readOnly = true)
	public JobApplicationResponse getApplication(Long applicationId) {
		return toResponse(applicationRepository.getRequiredWithDetails(applicationId));
	}

	@Transactional(readOnly = true)
	public List<JobApplicationSummaryResponse> getApplications(ApplicationStatus status) {
		List<JobApplication> applications = status == null
				? applicationRepository.findAllByOrderByIdAsc()
				: applicationRepository.findByStatusOrderByIdAsc(status);
		return applications.stream()
				.map(application -> new JobApplicationSummaryResponse(application.getId(),
						application.getCompany().getName(), application.getRoleTitle(), application.getStatus(),
						application.getAppliedAt()))
				.toList();
	}

	private Company findOrCreateCompany(String name) {
		return companyRepository.findByNameIgnoreCase(name).orElseGet(() -> {
			Company created = new Company();
			created.setName(name);
			return companyRepository.save(created);
		});
	}

	/**
	 * Devuelve la competencia de cada clave, creando las que no existen. Si una competencia ya existía sin
	 * categoría y ahora llega una, se le asigna (el cambio se guarda al cerrar la transacción).
	 */
	private Map<String, Skill> findOrCreateSkills(Map<String, String> namesByKey, Map<String, String> categoriesByKey) {
		Map<String, Skill> skills = new HashMap<>();
		if (namesByKey.isEmpty()) {
			return skills;
		}
		skillRepository.findAllByNameInIgnoreCase(namesByKey.values())
				.forEach(skill -> skills.put(SkillNames.key(skill.getName()), skill));
		namesByKey.forEach((key, name) -> {
			Skill skill = skills.computeIfAbsent(key, ignored -> {
				Skill created = new Skill();
				created.setName(name);
				created.setCategory(categoriesByKey.get(key));
				return skillRepository.save(created);
			});
			if (skill.getCategory() == null && categoriesByKey.containsKey(key)) {
				skill.setCategory(categoriesByKey.get(key));
			}
		});
		return skills;
	}

	private Map<String, String> normalizeCategories(Map<String, String> categories) {
		Map<String, String> normalized = new HashMap<>();
		categories.forEach((name, category) -> {
			if (name != null && !name.isBlank() && category != null && !category.isBlank()) {
				normalized.put(SkillNames.key(name), category.trim());
			}
		});
		return normalized;
	}

	private JobApplicationResponse toResponse(JobApplication application) {
		Set<String> required = new LinkedHashSet<>();
		Set<String> optional = new LinkedHashSet<>();
		application.getSkills()
				.forEach(link -> (link.isRequired() ? required : optional).add(link.getSkill().getName()));
		return new JobApplicationResponse(application.getId(), application.getCompany().getName(),
				application.getRoleTitle(), application.getJobDescription(), application.getJobUrl(),
				application.getSalaryMin(), application.getSalaryMax(), application.getSalaryCurrency(),
				application.getStatus(), application.getAppliedAt(), application.getAdditionalNotes(), required,
				optional);
	}

	private String appendNote(String current, String addition) {
		return current == null || current.isBlank() ? addition : current + System.lineSeparator() + addition;
	}
}
