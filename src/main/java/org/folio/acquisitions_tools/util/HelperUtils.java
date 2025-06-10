package org.folio.acquisitions_tools.util;

import java.util.regex.Pattern;

import io.micrometer.common.util.StringUtils;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HelperUtils {

	private static final Pattern TENANT_ID_PATTERN = Pattern.compile("^[a-z][a-z0-9_]{0,29}[a-z0-9]$");

	public static void validateTenantId(String tenantId) {
		if (StringUtils.isEmpty(tenantId) || !TENANT_ID_PATTERN.matcher(tenantId).matches()) {
			throw new IllegalArgumentException("Invalid tenant ID format: " + tenantId);
		}
	}

}
