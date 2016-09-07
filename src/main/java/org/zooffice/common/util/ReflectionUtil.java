package org.zooffice.common.util;

import static org.zooffice.common.util.NoOp.noOp;
import static org.zooffice.common.util.Preconditions.checkArgument;
import static org.zooffice.common.util.Preconditions.checkNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reflection Utility functions.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public abstract class ReflectionUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtil.class);

	/**
	 * get object field value, bypassing getter method.
	 * 
	 * @param object
	 *            object
	 * @param fieldName
	 *            field Name
	 * @return fileValue
	 */
	public static Object getFieldValue(final Object object, final String fieldName) {
		Field field = getDeclaredField(object, fieldName);
		checkNotNull(field, "Could not find field [%s] on target [%s]", fieldName, object);
		makeAccessible(field);

		try {
			return field.get(object);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	private static Field getDeclaredField(final Object object, final String fieldName) {
		checkNotNull(object);
		checkArgument(StringUtils.isNotBlank(fieldName));

		// CHECKSTYLE:OFF
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
						.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// Fall through
				noOp();
			}
		}
		return null;
	}

	private static void makeAccessible(final Field field) {
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 * Get the instance of the given class.
	 * 
	 * @param <T>
	 *            the return type
	 * @param className
	 *            class name including package name
	 * @return created instance
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstanceByName(String className) {
		try {
			Class<?> loader = Class.forName(className);
			return (T) loader.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
