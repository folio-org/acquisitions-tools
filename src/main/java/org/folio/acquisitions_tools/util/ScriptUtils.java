package org.folio.acquisitions_tools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import lombok.val;

@Log4j2
@UtilityClass
public class ScriptUtils {

	private static final String SQL_SCRIPTS_ROOT = "classpath:templates/db_scripts/";

	public static String readScript(String path, String tenantId) {
		return readScript(path).replaceAll("\\$\\{tenant_id}", tenantId);
	}

	public static String readScript(String path) {
		try {
			val resource = new PathMatchingResourcePatternResolver().getResource(SQL_SCRIPTS_ROOT + path);
			return new BufferedReader(new InputStreamReader(resource.getInputStream()))
					.lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			log.error("Failed to read script content: {}", path, e);
			throw new IllegalArgumentException(path, e);
		}
	}

}
