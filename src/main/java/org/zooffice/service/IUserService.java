package org.zooffice.service;

import org.zooffice.model.Role;
import org.zooffice.model.User;

/**
 * User service interface. This interface is visible to plugins
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public interface IUserService {

	/**
	 * Encode password of the given user.
	 * 
	 * @param user
	 *            user
	 */
	public abstract void encodePassword(User user);

	/**
	 * create user.
	 * 
	 * @param user
	 *            include id, userID, fullName, role, password.
	 * 
	 * @return result
	 */
	public abstract User saveUser(User user);

	/**
	 * Add user.
	 * 
	 * @param user
	 *            user
	 * @param role
	 *            role
	 */
	public abstract void saveUser(User user, Role role);

}