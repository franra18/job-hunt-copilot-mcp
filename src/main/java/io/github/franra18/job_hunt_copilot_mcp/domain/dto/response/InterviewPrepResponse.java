package io.github.franra18.job_hunt_copilot_mcp.domain.dto.response;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.QuestionDifficulty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterviewPrepResponse {
	private Long applicationId;
	private String roleTitle;
	private List<Question> questions;

	@Data
	@AllArgsConstructor
	public static class Question {
		private Long id;
		private String skill;
		private String question;
		private String suggestedAnswerPoints;
		private QuestionDifficulty difficulty;
	}
}
