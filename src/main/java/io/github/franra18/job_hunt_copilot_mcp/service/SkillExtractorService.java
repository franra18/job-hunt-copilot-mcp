package io.github.franra18.job_hunt_copilot_mcp.service;

import io.github.franra18.job_hunt_copilot_mcp.domain.dto.response.SkillExtractionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillExtractorService {
	private final ObjectProvider<ChatClient> chatClientProvider;

	public SkillExtractionResponse extract(String text) {
		if (text == null || text.isBlank()) {
			throw new IllegalArgumentException("jobDescription es obligatorio");
		}
		try {
			SkillExtractionResponse response = chatClientProvider.getObject().prompt()
					.system("Extrae competencias técnicas de una oferta. Separa requisitos obligatorios y deseables. "
							+ "Devuelve solo el objeto estructurado y estandariza nombres equivalentes a su forma canonica "
							+ "(Postgres -> PostgreSQL, ReactJS -> React, Node.js -> Node.js). "
							+ "Para cada skill incluye una categoria en skillCategories, usando una de estas categorias: "
							+ "lenguaje, framework, base de datos, cloud, devops, herramienta, metodologia u otra. "
							+ "Las claves de skillCategories deben coincidir con el nombre canonico de la skill.")
					.user(text)
					.call()
					.entity(SkillExtractionResponse.class);
			if (response == null) throw new IllegalStateException("La extracción de competencias no devolvió ninguna respuesta");
			return response;
		} catch (RuntimeException exception) {
			throw new IllegalStateException("No se pudieron extraer competencias de la descripción de la oferta", exception);
		}
	}
}
