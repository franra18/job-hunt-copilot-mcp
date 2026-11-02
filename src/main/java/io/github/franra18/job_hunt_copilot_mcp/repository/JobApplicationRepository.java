package io.github.franra18.job_hunt_copilot_mcp.repository;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.SkillRejectionStatsResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.JobApplication;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

	@EntityGraph(attributePaths = "company")
	List<JobApplication> findAllByOrderByIdAsc();

	@EntityGraph(attributePaths = "company")
	List<JobApplication> findByStatusOrderByIdAsc(ApplicationStatus status);

	@EntityGraph(attributePaths = {"company", "skills", "skills.skill"})
	Optional<JobApplication> findWithDetailsById(Long id);

	@Query("select new io.github.franra18.job_hunt_copilot_mcp.repository.StatusCount(a.status, count(a)) "
			+ "from JobApplication a group by a.status")
	List<StatusCount> countByStatus();

	@Query("select new io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.SkillRejectionStatsResponse(s.name, count(distinct a.id)) "
			+ "from JobApplication a join a.skills jas join jas.skill s "
			+ "where a.status = :status "
			+ "group by s.name order by count(distinct a.id) desc, s.name asc")
	List<SkillRejectionStatsResponse> countApplicationsBySkill(ApplicationStatus status);

	default JobApplication getRequired(Long id) {
		return findById(requireId(id)).orElseThrow(() -> notFound(id));
	}

	/** Igual que {@link #getRequired(Long)} pero cargando empresa y competencias en la misma consulta. */
	default JobApplication getRequiredWithDetails(Long id) {
		return findWithDetailsById(requireId(id)).orElseThrow(() -> notFound(id));
	}

	private static Long requireId(Long id) {
		if (id == null || id <= 0) {
			throw new IllegalArgumentException("applicationId debe ser un número positivo");
		}
		return id;
	}

	private static IllegalArgumentException notFound(Long id) {
		return new IllegalArgumentException("No se encontró la candidatura: " + id);
	}
}
