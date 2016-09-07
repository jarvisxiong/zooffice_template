
package org.zooffice.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zooffice.model.User;
import org.zooffice.user.repository.UserRepository;

/**
 * Customized login authentication filter. This checks not only auth but also timezone and
 * locale.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public class ZoofficeUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Constructor.
	 */
	public ZoofficeUsernamePasswordAuthenticationFilter() {
		super();
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = getAuthentification(request, response);
		String timezone = (String) request.getParameter("user_timezone");
		String language = (String) request.getParameter("native_language");
		SecuredUser securedUser = (SecuredUser) auth.getPrincipal();
		User user = securedUser.getUser();
		User existingUser = userRepository.findOneByUserId(user.getUserId());
		if (existingUser != null) {
			user = existingUser;
		}
		user.setTimeZone(timezone);
		user.setUserLanguage(language);
		securedUser.setUser(userRepository.saveAndFlush(user));
		return auth;
	}

	protected Authentication getAuthentification(HttpServletRequest request, HttpServletResponse response) {
		return super.attemptAuthentication(request, response);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
					javax.servlet.FilterChain chain, Authentication authResult) throws IOException, ServletException {
		super.successfulAuthentication(request, response, chain, authResult);
	};

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException failed) throws IOException, ServletException {
		super.unsuccessfulAuthentication(request, response, failed);
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

}
