package org.zooffice.common.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;

/**
 * Utility for Url manipulation.
 * 
 * @author JunHo Yoon
 * @since 3.2
 */
public abstract class UrlUtils {
	/**
	 * Get host part of the given url.
	 * 
	 * @param url
	 *            url
	 * @return host name
	 */
	public static String getHost(String url) {
		try {
			if (!url.startsWith("http")) {
				url = "http://" + url;
			}
			return StringUtils.trim(new URL(url).getHost());
		} catch (MalformedURLException e) {
			return "";
		}
	}
}
