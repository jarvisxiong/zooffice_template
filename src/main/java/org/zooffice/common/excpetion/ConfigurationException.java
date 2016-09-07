package org.zooffice.common.excpetion;

/**
 * Configuration Exception.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@SuppressWarnings("serial")
public class ConfigurationException extends ZoofficeRuntimeException {

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            message
	 * @param t
	 *            root cause
	 */
	public ConfigurationException(String message, Throwable t) {
		super(message, t);
	}
}
