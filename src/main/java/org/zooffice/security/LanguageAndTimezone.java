
package org.zooffice.security;

/**
 * DataHolder for user language and timezone.
 * 
 * @author JunHo Yoon
 * @since 3.0.2
 */
public class LanguageAndTimezone {
	private String language;
	private String timezone;

	/**
	 * Constructor.
	 * 
	 * @param locale
	 *            locale string
	 * @param timezone
	 *            timezone string
	 */
	public LanguageAndTimezone(String locale, String timezone) {
		this.setLanguage(locale);
		this.timezone = timezone;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
