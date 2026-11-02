package io.github.franra18.job_hunt_copilot_mcp.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Único punto donde se normalizan los nombres de competencias, para que "React", "react " y "REACT" cuenten
 * como la misma.
 */
final class SkillNames {

	private SkillNames() {
	}

	static String key(String name) {
		return name.trim().toLowerCase(Locale.ROOT);
	}

	/**
	 * Descarta nulos y vacíos, recorta espacios y elimina duplicados sin distinguir mayúsculas.
	 *
	 * @return mapa clave normalizada -> primera grafía recibida, en orden de llegada
	 */
	static Map<String, String> clean(Collection<String> names) {
		Map<String, String> result = new LinkedHashMap<>();
		if (names != null) {
			names.stream()
					.filter(name -> name != null && !name.isBlank())
					.map(String::trim)
					.forEach(name -> result.putIfAbsent(key(name), name));
		}
		return result;
	}
}
