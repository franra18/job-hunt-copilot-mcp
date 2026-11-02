package io.github.franra18.job_hunt_copilot_mcp.service;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.ai.GeneratedInterviewQuestions;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.InterviewPrepResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.InterviewPrepQuestion;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.JobApplication;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.QuestionDifficulty;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.Skill;
import io.github.franra18.job_hunt_copilot_mcp.repository.InterviewPrepQuestionRepository;
import io.github.franra18.job_hunt_copilot_mcp.repository.JobApplicationRepository;
import io.github.franra18.job_hunt_copilot_mcp.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewPrepService {
	private static final String SYSTEM_PROMPT = "Genera preguntas tecnicas dificiles para una entrevista. Cubre "
			+ "escenarios practicos, trade-offs y debugging. Devuelve solo el objeto estructurado con entre 4 y 6 "
			+ "preguntas. Cada pregunta debe incluir skillName, question, suggestedAnswerPoints y difficulty "
			+ "(EASY, MEDIUM o HARD).";

	private final InterviewPrepQuestionRepository questionRepository;
	private final JobApplicationRepository applicationRepository;
	private final SkillRepository skillRepository;
	private final ObjectProvider<ChatClient> chatClientProvider;
	private final TransactionTemplate transactionTemplate;

	/**
	 * Genera preguntas nuevas y sustituye las guardadas. La llamada al LLM se hace fuera de cualquier transacción
	 * para no retener una conexión a la base de datos mientras se espera la respuesta.
	 */
	public InterviewPrepResponse generateQuestions(Long applicationId) {
		String userPrompt = transactionTemplate.execute(
				status -> buildUserPrompt(applicationRepository.getRequiredWithDetails(applicationId)));
		List<GeneratedInterviewQuestions.Question> generated = askModel(applicationId, userPrompt);
		return transactionTemplate.execute(status -> {
			JobApplication application = applicationRepository.getRequired(applicationId);
			application.getInterviewPrepQuestions().clear();
			generated.forEach(item -> application.addInterviewPrepQuestion(toEntity(item)));
			applicationRepository.flush();
			return getPreparation(applicationId);
		});
	}

	@Transactional(readOnly = true)
	public InterviewPrepResponse getPreparation(Long applicationId) {
		JobApplication application = applicationRepository.getRequired(applicationId);
		List<InterviewPrepResponse.Question> questions = questionRepository
				.findByApplicationIdOrderByIdAsc(applicationId).stream()
				.map(question -> new InterviewPrepResponse.Question(question.getId(),
						question.getSkill() == null ? null : question.getSkill().getName(), question.getQuestion(),
						question.getSuggestedAnswerPoints(), question.getDifficulty()))
				.toList();
		return new InterviewPrepResponse(applicationId, application.getRoleTitle(), questions);
	}

	private String buildUserPrompt(JobApplication application) {
		String stack = application.getSkills().stream()
				.map(link -> link.getSkill().getName() + (link.isRequired() ? " (obligatoria)" : " (deseable)"))
				.sorted()
				.collect(Collectors.joining(", "));
		return "Puesto: " + application.getRoleTitle()
				+ "\nStack: " + (stack.isEmpty() ? "sin competencias registradas" : stack)
				+ "\nDescripcion: " + application.getJobDescription();
	}

	private List<GeneratedInterviewQuestions.Question> askModel(Long applicationId, String userPrompt) {
		GeneratedInterviewQuestions generated;
		try {
			generated = chatClientProvider.getObject().prompt()
					.system(SYSTEM_PROMPT)
					.user(userPrompt)
					.call()
					.entity(GeneratedInterviewQuestions.class);
		} catch (RuntimeException exception) {
			throw new IllegalStateException(
					"No se pudieron generar preguntas de entrevista para la candidatura " + applicationId, exception);
		}
		List<GeneratedInterviewQuestions.Question> valid = generated == null || generated.questions() == null
				? List.of()
				: generated.questions().stream()
						.filter(item -> item != null && item.question() != null && !item.question().isBlank())
						.toList();
		if (valid.isEmpty()) {
			throw new IllegalStateException("El modelo no devolvió preguntas para la candidatura " + applicationId);
		}
		return valid;
	}

	private InterviewPrepQuestion toEntity(GeneratedInterviewQuestions.Question item) {
		InterviewPrepQuestion entity = new InterviewPrepQuestion();
		entity.setQuestion(item.question().trim());
		entity.setSuggestedAnswerPoints(item.suggestedAnswerPoints());
		entity.setDifficulty(item.difficulty() == null ? QuestionDifficulty.MEDIUM : item.difficulty());
		if (item.skillName() != null && !item.skillName().isBlank()) {
			entity.setSkill(findOrCreateSkill(item.skillName().trim()));
		}
		return entity;
	}

	private Skill findOrCreateSkill(String name) {
		return skillRepository.findByNameIgnoreCase(name).orElseGet(() -> {
			Skill created = new Skill();
			created.setName(name);
			return skillRepository.save(created);
		});
	}
}
