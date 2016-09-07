package org.zooffice.common.excpetion;

/**
 * ZoofficeRuntimeException. This is for translating a general exception to {@link RuntimeException}
 * .
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public class ZoofficeRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 8662535812004958944L;

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            message
	 */
	public ZoofficeRuntimeException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            message
	 * @param t
	 *            root cause
	 */
	public ZoofficeRuntimeException(String message, Throwable t) {
		super(message, t);
	}
}
