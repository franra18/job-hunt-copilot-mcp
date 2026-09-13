package io.github.franra18.job_hunt_copilot_mcp.repository;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.Company;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
	Optional<Company> findByNameIgnoreCase(String name);
}
