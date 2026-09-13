package io.github.franra18.job_hunt_copilot_mcp.mcp.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.franra18.job_hunt_copilot_mcp.mcp.tools.AnalyticsTools;
import io.github.franra18.job_hunt_copilot_mcp.mcp.tools.GapAnalysisTools;
import io.github.franra18.job_hunt_copilot_mcp.mcp.tools.InterviewPrepTools;
import io.github.franra18.job_hunt_copilot_mcp.mcp.tools.JobApplicationTools;

@Configuration
public class McpConfig {
	@Bean
	ChatClient chatClient(ChatClient.Builder builder) {
		return builder.build();
	}

	@Bean
	ToolCallbackProvider jobHuntTools(JobApplicationTools jobApplicationTools,
			InterviewPrepTools interviewPrepTools, AnalyticsTools analyticsTools, GapAnalysisTools gapAnalysisTools) {
		return MethodToolCallbackProvider.builder()
				.toolObjects(jobApplicationTools, interviewPrepTools, analyticsTools, gapAnalysisTools)
				.build();
	}
}
