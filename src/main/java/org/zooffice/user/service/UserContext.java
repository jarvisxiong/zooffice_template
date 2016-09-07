package org.zooffice.user.service;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zooffice.infra.annotation.RuntimeOnlyComponent;
import org.zooffice.model.User;
import org.zooffice.security.SecuredUser;

/**
 * User Context which return current user.
 * 
 * @author Tobi
 * @author JunHo Yoon
 * @since 3.0
 */
@RuntimeOnlyComponent
public class UserContext {


	/**
	 * Get current user object from context.
	 * 
	 * @return current user;
	 */
	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
			throw new AuthenticationCredentialsNotFoundException("No athenticated");
		}
		Object obj = auth.getPrincipal();
		if (!(obj instanceof SecuredUser)) {
			throw new AuthenticationCredentialsNotFoundException("Invalid athentication with " +  obj);
		}
		SecuredUser securedUser = (SecuredUser) obj;
		return securedUser.getUser();
	}
}
