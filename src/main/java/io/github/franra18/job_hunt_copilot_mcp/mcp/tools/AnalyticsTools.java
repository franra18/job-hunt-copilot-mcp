package io.github.franra18.job_hunt_copilot_mcp.mcp.tools;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.AnalyticsSummaryResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.AnalyticsMetricType;
import io.github.franra18.job_hunt_copilot_mcp.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsTools {

	private final AnalyticsService analyticsService;

	@Tool(name = "obtener_resumen_metricas",
			description = "Devuelve métricas de candidaturas: embudo, competencias de candidaturas rechazadas o ambas")
	public AnalyticsSummaryResponse getSummary(
			@ToolParam(description = "Tipo de métricas: ALL, FUNNEL o REJECTIONS", required = false) AnalyticsMetricType type) {
		return analyticsService.getSummary(type);
	}
}
