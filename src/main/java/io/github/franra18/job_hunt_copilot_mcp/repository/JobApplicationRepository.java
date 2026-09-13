package io.github.franra18.job_hunt_copilot_mcp.repository;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.JobApplication;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
	List<JobApplication> findByStatus(ApplicationStatus status);
	List<JobApplication> findByCompanyId(Long companyId);

	@Query("select a.status, count(a) from JobApplication a group by a.status")
	List<Object[]> countByStatus();

	@Query("select s.name, count(distinct a.id) "
			+ "from JobApplication a join a.skills jas join jas.skill s "
			+ "where a.status = :rejectedStatus "
			+ "group by s.name order by count(distinct a.id) desc, s.name asc")
	List<Object[]> countRejectedApplicationsBySkill(ApplicationStatus rejectedStatus);
}
