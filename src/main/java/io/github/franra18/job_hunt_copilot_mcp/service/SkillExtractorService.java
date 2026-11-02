package io.github.franra18.job_hunt_copilot_mcp.service;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.ai.SkillExtractionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillExtractorService {
	private static final String SYSTEM_PROMPT = "Extrae competencias técnicas de una oferta. Separa requisitos "
			+ "obligatorios y deseables. Devuelve solo el objeto estructurado y estandariza nombres equivalentes a su "
			+ "forma canonica (Postgres -> PostgreSQL, ReactJS -> React, Node.js -> Node.js). "
			+ "Para cada skill incluye una categoria en skillCategories, usando una de estas categorias: "
			+ "lenguaje, framework, base de datos, cloud, devops, herramienta, metodologia u otra. "
			+ "Las claves de skillCategories deben coincidir con el nombre canonico de la skill.";

	private final ObjectProvider<ChatClient> chatClientProvider;

	public SkillExtractionResult extract(String text) {
		if (text == null || text.isBlank()) {
			throw new IllegalArgumentException("jobDescription es obligatorio");
		}
		SkillExtractionResult response;
		try {
			response = chatClientProvider.getObject().prompt()
					.system(SYSTEM_PROMPT)
					.user(text)
					.call()
					.entity(SkillExtractionResult.class);
		} catch (RuntimeException exception) {
			throw new IllegalStateException("No se pudieron extraer competencias de la descripción de la oferta",
					exception);
		}
		if (response == null) {
			throw new IllegalStateException("La extracción de competencias no devolvió ninguna respuesta");
		}
		return response;
	}
}
