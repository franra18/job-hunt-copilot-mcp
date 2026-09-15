package io.github.franra18.job_hunt_copilot_mcp.service;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.GeneratedInterviewQuestionsResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.InterviewPrepResponse;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.InterviewPrepQuestion;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.JobApplication;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.QuestionDifficulty;
import io.github.franra18.job_hunt_copilot_mcp.domain.model.Skill;
import io.github.franra18.job_hunt_copilot_mcp.repository.InterviewPrepQuestionRepository;
import io.github.franra18.job_hunt_copilot_mcp.repository.JobApplicationRepository;
import io.github.franra18.job_hunt_copilot_mcp.repository.SkillRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InterviewPrepService {
	private final InterviewPrepQuestionRepository questionRepository;
	private final JobApplicationRepository applicationRepository;
	private final SkillRepository skillRepository;
	private final ObjectProvider<ChatClient> chatClientProvider;

	@Transactional
	public InterviewPrepResponse generateQuestions(Long applicationId) {
		JobApplication application = getApplication(applicationId);
		String stack = application.getSkills().stream()
				.map(link -> link.getSkill().getName() + (link.isRequired() ? " (obligatoria)" : " (deseable)"))
				.sorted()
				.reduce((left, right) -> left + ", " + right)
				.orElse("sin competencias registradas");
		GeneratedInterviewQuestionsResponse generated;
		try {
			generated = chatClientProvider.getObject().prompt()
					.system("Genera preguntas tecnicas dificiles para una entrevista. Cubre escenarios practicos, "
							+ "trade-offs y debugging. Devuelve solo el objeto estructurado con entre 4 y 6 preguntas. "
							+ "Cada pregunta debe incluir skillName, question, suggestedAnswerPoints y difficulty "
							+ "(EASY, MEDIUM o HARD).")
					.user("Puesto: " + application.getRoleTitle() + "\nStack: " + stack + "\nDescripcion: "
							+ application.getJobDescription())
					.call()
					.entity(GeneratedInterviewQuestionsResponse.class);
		} catch (RuntimeException exception) {
			throw new IllegalStateException("No se pudieron generar preguntas de entrevista para la candidatura " + applicationId,
					exception);
		}
		if (generated != null && generated.questions() != null) {
			generated.questions().stream().filter(item -> item != null && item.question() != null
					&& !item.question().isBlank()).forEach(item -> saveGeneratedQuestion(application, item));
		}
		return getPreparation(applicationId);
	}

	@Transactional(readOnly = true)
	public InterviewPrepResponse getPreparation(Long applicationId) {
		JobApplication application = getApplication(applicationId);
		List<InterviewPrepResponse.Question> questions = questionRepository.findByApplicationId(applicationId).stream()
				.map(question -> new InterviewPrepResponse.Question(question.getId(),
						question.getSkill() == null ? null : question.getSkill().getName(), question.getQuestion(),
						question.getSuggestedAnswerPoints(), question.getDifficulty()))
				.toList();
		return new InterviewPrepResponse(applicationId, application.getRoleTitle(), questions);
	}

	private JobApplication getApplication(Long id) {
		return applicationRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("No se encontró la candidatura: " + id));
	}

	private void saveGeneratedQuestion(JobApplication application, GeneratedInterviewQuestionsResponse.Question item) {
		InterviewPrepQuestion entity = new InterviewPrepQuestion();
		entity.setApplication(application);
		entity.setQuestion(item.question().trim());
		entity.setSuggestedAnswerPoints(item.suggestedAnswerPoints());
		entity.setDifficulty(item.difficulty() == null ? QuestionDifficulty.MEDIUM : item.difficulty());
		if (item.skillName() != null && !item.skillName().isBlank()) {
			Skill skill = skillRepository.findByNameIgnoreCase(item.skillName().trim()).orElseGet(() -> {
				Skill created = new Skill();
				created.setName(item.skillName().trim());
				return skillRepository.save(created);
			});
			entity.setSkill(skill);
		}
		application.getInterviewPrepQuestions().add(entity);
	}
}
