package io.github.franra18.job_hunt_copilot_mcp.repository;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.Skill;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
	Optional<Skill> findByNameIgnoreCase(String name);

	List<Skill> findAllByNameInIgnoreCase(Collection<String> names);
}
