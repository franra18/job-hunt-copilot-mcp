package io.github.franra18.job_hunt_copilot_mcp.service;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.request.CreateApplicationRequest;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.JobApplicationResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.JobApplicationSummaryResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.Company;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.JobApplication;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.JobApplicationSkill;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.Skill;
import io.github.franra18.job_hunt_copilot_mcp.repository.CompanyRepository;
import io.github.franra18.job_hunt_copilot_mcp.repository.JobApplicationRepository;
import io.github.franra18.job_hunt_copilot_mcp.repository.SkillRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobApplicationService {
	private final JobApplicationRepository applicationRepository;
	private final CompanyRepository companyRepository;
	private final SkillRepository skillRepository;

	@Transactional
	public JobApplicationResponse createApplication(CreateApplicationRequest request) {
		requireText(request.getCompanyName(), "companyName");
		requireText(request.getRoleTitle(), "roleTitle");
		Company company = companyRepository.findByNameIgnoreCase(request.getCompanyName().trim())
				.orElseGet(() -> {
					Company created = new Company();
					created.setName(request.getCompanyName().trim());
					return companyRepository.save(created);
				});

		JobApplication application = new JobApplication();
		application.setCompany(company);
		application.setRoleTitle(request.getRoleTitle().trim());
		application.setJobDescription(request.getJobDescription());
		application.setJobUrl(request.getJobUrl());
		application.setSalaryMin(request.getSalaryMin());
		application.setSalaryMax(request.getSalaryMax());
		application.setSalaryCurrency(request.getSalaryCurrency());
		application.setAdditionalNotes(request.getAdditionalNotes());
		application.setStatus(ApplicationStatus.APPLIED);
		application.setAppliedAt(LocalDate.now());
		application = applicationRepository.save(application);

		Set<String> required = new LinkedHashSet<>(request.getRequiredSkills() == null ? Set.of() : request.getRequiredSkills());
		Set<String> optional = new LinkedHashSet<>(request.getOptionalSkills() == null ? Set.of() : request.getOptionalSkills());
		Map<String, String> categories = request.getSkillCategories() == null ? Map.of() : request.getSkillCategories();
		optional.removeAll(required);
		addSkills(application, required, true, categories);
		addSkills(application, optional, false, categories);
		application = applicationRepository.save(application);
		return toResponse(application);
	}

	@Transactional
	public JobApplicationResponse updateStatus(Long applicationId, ApplicationStatus status, String interviewNotes) {
		JobApplication application = getEntity(applicationId);
		application.setStatus(status);
		if (interviewNotes != null && !interviewNotes.isBlank()) {
			application.setAdditionalNotes(appendNote(application.getAdditionalNotes(), interviewNotes));
		}
		application = applicationRepository.save(application);
		return toResponse(application);
	}

	@Transactional(readOnly = true)
	public JobApplicationResponse getApplication(Long applicationId) {
		return toResponse(getEntity(applicationId));
	}

	@Transactional(readOnly = true)
	public List<JobApplicationSummaryResponse> getApplications(ApplicationStatus status) {
		List<JobApplication> applications = status == null
				? applicationRepository.findAll()
				: applicationRepository.findByStatus(status);
		return applications.stream()
				.map(application -> new JobApplicationSummaryResponse(application.getId(), application.getCompany().getName(),
						application.getRoleTitle(), application.getStatus(), application.getAppliedAt()))
				.toList();
	}

	private void addSkills(JobApplication application, Set<String> names, boolean required,
			Map<String, String> categoriesByName) {
		Map<String, String> normalizedNames = new java.util.LinkedHashMap<>();
		Map<String, String> normalizedCategories = new java.util.HashMap<>();
		categoriesByName.forEach((name, category) -> {
			if (name != null && category != null && !name.isBlank() && !category.isBlank()) {
				normalizedCategories.put(name.trim().toLowerCase(Locale.ROOT), category.trim());
			}
		});
		names.stream().filter(name -> name != null && !name.isBlank()).map(String::trim)
				.forEach(name -> normalizedNames.putIfAbsent(name.toLowerCase(Locale.ROOT), name));
		Map<String, Skill> skills = new HashMap<>();
		skillRepository.findAllByNameInIgnoreCase(normalizedNames.values()).forEach(skill ->
				skills.put(skill.getName().toLowerCase(Locale.ROOT), skill));
		List<Skill> missing = normalizedNames.entrySet().stream().filter(entry -> !skills.containsKey(entry.getKey()))
				.map(entry -> {
					Skill created = new Skill();
					created.setName(entry.getValue());
					created.setCategory(normalizedCategories.get(entry.getKey()));
					return created;
				}).toList();
		skillRepository.saveAll(missing).forEach(skill -> skills.put(skill.getName().toLowerCase(Locale.ROOT), skill));
		normalizedNames.keySet().forEach(key -> {
				Skill skill = skills.get(key);
			if (skill.getCategory() == null && normalizedCategories.containsKey(key)) {
				skill.setCategory(normalizedCategories.get(key));
				skillRepository.save(skill);
			}
					JobApplicationSkill link = new JobApplicationSkill();
					link.setApplication(application);
					link.setSkill(skill);
					link.setRequired(required);
					application.getSkills().add(link);
				});
	}

	private JobApplication getEntity(Long id) {
		return applicationRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No se encontró la candidatura: " + id));
	}

	private JobApplicationResponse toResponse(JobApplication application) {
		Set<String> required = new LinkedHashSet<>();
		Set<String> optional = new LinkedHashSet<>();
		application.getSkills().forEach(link -> (link.isRequired() ? required : optional).add(link.getSkill().getName()));
		return new JobApplicationResponse(application.getId(), application.getCompany().getName(), application.getRoleTitle(),
				application.getJobDescription(), application.getJobUrl(), application.getSalaryMin(), application.getSalaryMax(),
				application.getSalaryCurrency(), application.getStatus(), application.getAppliedAt(), application.getAdditionalNotes(),
				required, optional);
	}

	private String appendNote(String current, String addition) {
		return current == null || current.isBlank() ? addition : current + System.lineSeparator() + addition;
	}

	private void requireText(String value, String field) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(field + " es obligatorio");
		}
	}
}
