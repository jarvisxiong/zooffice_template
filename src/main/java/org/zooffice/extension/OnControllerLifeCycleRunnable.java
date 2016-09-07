package org.zooffice.extension;
/**
 * Plugin extension point which is executable when controller start and finish.
 * 
 * This plugin is necessary if you want to notify the controller start and end.
 * 
 * @author JunHo Yoon
 * @since 1.0
 */
public interface OnControllerLifeCycleRunnable {

	/**
	 * Callback method which will be invoked whenever Controller is started.
	 * 
	 * 
	 * @param ip
	 *            ip
	 * @param version
	 *            version
	 */
	public void start(String ip, String version);

	/**
	 * Callback method which will be invoked whenever Controller is stopped.
	 * 
	 * 
	 * @param ip
	 *            ip
	 * @param version
	 *            version
	 */
	public void finish(String ip, String version);
}