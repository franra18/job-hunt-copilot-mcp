package io.github.franra18.job_hunt_copilot_mcp.domain.dto.ai;

import io.github.franra18.job_hunt_copilot_mcp.domain.model.QuestionDifficulty;

import java.util.List;

/**
 * Esquema de salida estructurada que se le pide al LLM al generar preguntas de entrevista.
 */
public record GeneratedInterviewQuestions(List<Question> questions) {

	public record Question(String skillName, String question, String suggestedAnswerPoints,
			QuestionDifficulty difficulty) {
	}
}
