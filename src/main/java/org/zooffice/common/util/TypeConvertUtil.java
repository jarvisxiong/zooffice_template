package org.zooffice.common.util;
/**
 * Convenient class for type conversion.
 * 
 * @author JunHo Yoon
 * @since 3.1
 */
public abstract class TypeConvertUtil {
	

	/**
	 * Convert object to return type.
	 * 
	 * @param object
	 *            object to be converted.
	 * @param <T>
	 *            converted type
	 * @return converted object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object object) {
		return (T) object;
	}
}
