package io.github.franra18.job_hunt_copilot_mcp.repository;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.UserSkill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
	List<UserSkill> findAllByOrderByNameAsc();
	java.util.Optional<UserSkill> findByNameIgnoreCase(String name);
}
