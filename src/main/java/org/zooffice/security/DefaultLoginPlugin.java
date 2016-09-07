
package org.zooffice.security;

import org.zooffice.extension.OnLoginRunnable;
import org.zooffice.model.User;
import org.zooffice.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.stereotype.Service;

/**
 * This is default Plugin.
 * @author nhn
 *
 */
@Service
public class DefaultLoginPlugin implements OnLoginRunnable {

	protected static final Logger LOG = LoggerFactory.getLogger(DefaultLoginPlugin.class);

	@Autowired
	private UserService userService;

	private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	@Override
	public User loadUser(String userId) {
		return userService.getUserById(userId);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean validateUser(String userId, String password, String encPass, Object encoder, Object salt) {
		if (!((PasswordEncoder) encoder).isPasswordValid(encPass, password, salt)) {
			LOG.debug("Authentication failed: password does not match stored value");

			throw new BadCredentialsException(messages.getMessage(
					"AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userId);
		}
		return true;
	}

	@Override
	public void saveUser(User user) {
		// Do nothing for default plugin
	}
}
