package io.github.franra18.job_hunt_copilot_mcp.repository;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.ApplicationStatus;

/**
 * Proyección del recuento de candidaturas por estado.
 */
public record StatusCount(ApplicationStatus status, Long total) {
}
