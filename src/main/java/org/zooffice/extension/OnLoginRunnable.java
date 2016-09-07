package org.zooffice.extension;

import org.zooffice.model.User;

/**
 * Plugin extension point for the custom user authentication.
 * 
 * @author JunHo Yoon
 * @since 1.0
 */
public interface OnLoginRunnable {
	/**
	 * Load user by userId.
	 * 
	 * @param userId
	 *            user id
	 * @return User instance
	 */
	public User loadUser(String userId);

	/**
	 * Validate user by userId and password.
	 * 
	 * Against password can be provided by plugin. In such case encPass, encoder, salt might be
	 * null.
	 * 
	 * @param userId
	 *            user providing id
	 * @param password
	 *            user providing password
	 * @param encPass
	 *            encrypted password
	 * @param encoder
	 *            encoder which encrypts password
	 * @param salt
	 *            salt of encoding
	 * @return true is validated
	 */
	public boolean validateUser(String userId, String password, String encPass, Object encoder, Object salt);

	/**
	 * Save user in plugin.<br/>
	 * This method is only necessary to implement if there is need to save the user in the plugin.
	 * Generally dummy implementation is enough.
	 * 
	 * @param user
	 *            user to be saved.
	 */
	public void saveUser(User user);
}
