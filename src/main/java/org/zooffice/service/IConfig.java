package org.zooffice.service;

import org.zooffice.common.util.PropertiesWrapper;

/**
 * Config access interface.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public interface IConfig {

	/**
	 * Check if it's testmode.
	 * 
	 * @return true if test mode
	 */
	public abstract boolean isTestMode();

	/**
	 * Check if it's the security enabled mode.
	 * 
	 * @return true if security is enabled.
	 */
	public abstract boolean isSecurityEnabled();

	/**
	 * Check if plugin support is enabled. The reason why we need this configuration is that it
	 * takes time to initialize plugin system in unit test context.
	 * 
	 * @return true if plugin is supported.
	 */
	public abstract boolean isPluginSupported();

	/**
	 * Get the system properties.
	 * 
	 * @return {@link PropertiesWrapper} which is loaded from system.conf.
	 */
	public abstract PropertiesWrapper getSystemProperties();

}