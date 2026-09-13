package io.github.franra18.job_hunt_copilot_mcp.repository;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.InterviewPrepQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewPrepQuestionRepository extends JpaRepository<InterviewPrepQuestion, Long> {
	List<InterviewPrepQuestion> findByApplicationId(Long applicationId);
}
