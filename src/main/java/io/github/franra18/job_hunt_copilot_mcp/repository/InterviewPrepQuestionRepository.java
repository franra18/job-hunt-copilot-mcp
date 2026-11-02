package io.github.franra18.job_hunt_copilot_mcp.repository;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.InterviewPrepQuestion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewPrepQuestionRepository extends JpaRepository<InterviewPrepQuestion, Long> {

	@EntityGraph(attributePaths = "skill")
	List<InterviewPrepQuestion> findByApplicationIdOrderByIdAsc(Long applicationId);
}
