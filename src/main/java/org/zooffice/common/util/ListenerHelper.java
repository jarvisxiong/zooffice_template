package org.zooffice.common.util;

/**
 * Listener Helper to shorten the {@link ListenerSupport} creation code.
 * 
 * @author JunHo Yoon
 * @since 3.0.2
 */
public abstract class ListenerHelper {
	/**
	 * Create a listener instance.
	 * 
	 * @param <T>
	 *            listener type.
	 * @return created listener
	 */
	public static <T> ListenerSupport<T> create() {
		return new ListenerSupport<T>();
	}
}
