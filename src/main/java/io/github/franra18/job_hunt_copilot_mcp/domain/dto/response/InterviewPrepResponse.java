package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.QuestionDifficulty;

import java.util.List;

public record InterviewPrepResponse(Long applicationId, String roleTitle, List<Question> questions) {

	public record Question(Long id, String skill, String question, String suggestedAnswerPoints,
			QuestionDifficulty difficulty) {
	}
}
