package io.github.franra18.job_hunt_copilot_mcp.mcp.tools;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.AnalyticsSummaryResponse;
import io.github.franra18.job_hunt_copilot_mcp.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsTools {

	private final AnalyticsService analyticsService;

	@Tool(name = "obtener_resumen_metricas",
			description = "Devuelve el embudo de candidaturas por estado y las competencias que más se repiten "
					+ "en las candidaturas rechazadas")
	public AnalyticsSummaryResponse getSummary() {
		return analyticsService.getSummary();
	}
}
