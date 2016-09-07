package org.zooffice.common.util;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for path manipulation.
 * 
 * @author JunHo Yoon
 * @since 3.0
 * 
 */
public abstract class PathUtil {

	public static final int MAX_PATH_LENGTH = 40;

	/**
	 * Remove prepending / on path.
	 * 
	 * @param path
	 *            path containning /
	 * @return / removed path
	 */
	public static String removePrependedSlash(String path) {
		if (path.startsWith("/")) {
			return path.substring(1);
		}
		return path;
	}

	/**
	 * Join two path.
	 * 
	 * @param path1
	 *            path1
	 * @param path2
	 *            path2
	 * 
	 * @return / removed path
	 */
	public static String join(String path1, String path2) {
		String path = (path1 + "/" + path2);
		if (path.startsWith("/")) {
			return path.substring(1);
		} else {
			return path;
		}
	}

	/**
	 * Remove prepending / on path.
	 * 
	 * @param path
	 *            path containning /
	 * @return / removed path
	 */
	public static String removeDuplicatedPrependedSlash(String path) {
		if (path.startsWith("//")) {
			return path.substring(1);
		}
		return path;
	}

	/**
	 * Get short path because actual path is too long to display it totally.
	 * 
	 * @param path
	 *            path
	 * 
	 * @return shortPath
	 */
	public static String getShortPath(String path) {
		if (path.length() >= MAX_PATH_LENGTH && StringUtils.contains(path, "/")) {
			String start = path.substring(0, path.indexOf("/") + 1);
			String end = path.substring(path.lastIndexOf("/"), path.length());
			return start + "..." + end;
		} else {
			return path;
		}
	}
}
