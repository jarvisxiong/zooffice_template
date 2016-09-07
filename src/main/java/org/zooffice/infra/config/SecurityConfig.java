
package org.zooffice.infra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

/**
 * Some User want to have more secured password. Provide the enhanced pw with sha256 if a user
 * specifies ngrinder.security.sha256 in system.conf
 * 
 * @author JunHo Yoon
 * 
 */
@Configuration
public class SecurityConfig {

	@Autowired
	private Config config;

	/**
	 * Provide the appropriate shaPasswordEncoder depending on the ngrinder.security.sha256 config.
	 * 
	 * @return {@link ShaPasswordEncoder} with 256 if ngrinder.security.sha256=true. Otherwise
	 *         returns default {@link ShaPasswordEncoder}
	 */
	@Bean(name = "shaPasswordEncoder")
	public ShaPasswordEncoder sharPasswordEncoder() {
		boolean useEnhancedEncoding = config.getSystemProperties()
						.getPropertyBoolean("ngrinder.security.sha256", false);
		return useEnhancedEncoding ? new ShaPasswordEncoder(256) : new ShaPasswordEncoder();
	}
}
