
package org.zooffice.infra.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide core logger which is always visible in LOG.
 * Even verbose mode is off, LOG reported by this Logger is always shown in log file.
 * 
 * This logger is subject to used to report the major execution steps of each perftest.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public abstract class CoreLogger {

	/**
	 * Core logger.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(CoreLogger.class);
}
