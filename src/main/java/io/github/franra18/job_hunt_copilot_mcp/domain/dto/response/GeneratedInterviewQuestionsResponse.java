package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.QuestionDifficulty;
import java.util.List;

public record GeneratedInterviewQuestionsResponse(List<Question> questions) {
	public record Question(String skillName, String question, String suggestedAnswerPoints,
			QuestionDifficulty difficulty) {
	}
}
