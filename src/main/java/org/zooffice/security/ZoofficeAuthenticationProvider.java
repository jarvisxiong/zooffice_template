
package org.zooffice.security;

import static org.zooffice.common.util.NoOp.noOp;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.zooffice.extension.OnLoginRunnable;
import org.zooffice.infra.plugin.PluginManager;
import org.zooffice.model.Role;
import org.zooffice.model.User;
import org.zooffice.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * nGrinder authentication provide. This class is for the plugin system of user authentication.
 * 
 * @author JunHo Yoon
 * @since 3.0
 * 
 */
@Service("zoofficeAuthenticationProvider")
public class ZoofficeAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	protected static final Logger LOG = LoggerFactory.getLogger(ZoofficeAuthenticationProvider.class);

	@Autowired
	private PluginManager pluginManager;

	@Autowired
	private DefaultLoginPlugin defaultLoginPlugin;
	// ~ Instance fields
	// ================================================================================================

	@Autowired
	@Qualifier("shaPasswordEncoder")
	private PasswordEncoder passwordEncoder;

	@Autowired
	@Qualifier("reflectionSaltSource")
	private SaltSource saltSource;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserService userService;

	// ~ Methods
	// ========================================================================================================

	@SuppressWarnings("deprecation")
	protected void additionalAuthenticationChecks(UserDetails userDetails,
					UsernamePasswordAuthenticationToken authentication) {

		Authentication authentication2 = SecurityContextHolder.getContext().getAuthentication();
		if (authentication2 != null) {
			return;
		}
		Object salt = null;

		if (this.saltSource != null) {
			salt = this.saltSource.getSalt(userDetails);
		}

		String message = messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials",
						"Bad credentials");
		if (authentication.getCredentials() == null) {
			LOG.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException(message, userDetails);
		}

		String presentedPassword = authentication.getCredentials().toString();
		SecuredUser user = ((SecuredUser) userDetails);
		boolean authorized = false;

		for (OnLoginRunnable each : getPluginManager().getEnabledModulesByClass(OnLoginRunnable.class,
						defaultLoginPlugin)) {

			if (isClassEqual(each.getClass(), defaultLoginPlugin.getClass().getName())) {
				if (StringUtils.isEmpty(user.getAuthProviderClass())
								|| isClassEqual(DefaultLoginPlugin.class, user.getUserInfoProviderClass())) {
					each.validateUser(user.getUsername(), presentedPassword, user.getPassword(), passwordEncoder, salt);
					authorized = true;
					break;
				} else {
					try {
						each.validateUser(user.getUsername(), presentedPassword, user.getPassword(), passwordEncoder,
										salt);
						authorized = true;
						break;
					} catch (Exception e) {
						noOp();
					}
				}
			} else if (isClassEqual(each.getClass(), user.getAuthProviderClass())) {
				each.validateUser(user.getUsername(), presentedPassword, user.getPassword(), passwordEncoder, salt);
				authorized = true;
				break;
			}

		}

		if (!authorized) {
			throw new BadCredentialsException(message, user);
		}

		// If It's the first time to login
		// means.. If the user info provider is not defaultLoginPlugin..
		if (user.getUserInfoProviderClass() != null
						&& !isClassEqual(defaultLoginPlugin.getClass(), user.getUserInfoProviderClass())) {
			addNewUserIntoLocal(user);
		}
	}

	/**
	 * Check if given clazz has the given clazzName.
	 * 
	 * @param clazz
	 *            class
	 * @param clazzName
	 *            classname which is checked aginst
	 * @return true if same
	 */
	private boolean isClassEqual(Class<?> clazz, String clazzName) {
		return clazz.getName().equals(clazzName);
	}

	/**
	 * Add new user into local db.
	 * 
	 * @param securedUser
	 *            user
	 */
	@Transactional
	public void addNewUserIntoLocal(SecuredUser securedUser) {
		User user = securedUser.getUser();
		user.setAuthProviderClass(securedUser.getUserInfoProviderClass());
		user.setCreatedDate(new Date());
		User newUser = userService.getUserById(user.getUserId());
		if (newUser != null) {
			user = newUser.merge(user);
		}
		if (user.getRole() == null) {
			user.setRole(Role.USER);
		}
		User savedUser = userService.saveUser(user);
		securedUser.setUser(savedUser);
	}

	protected void doAfterPropertiesSet() throws Exception {
		Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
	}

	protected final UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
		UserDetails loadedUser;

		try {
			loadedUser = this.getUserDetailsService().loadUserByUsername(username);
		} catch (UsernameNotFoundException notFound) {
			throw notFound;
		} catch (Exception repositoryProblem) {
			throw new AuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			throw new AuthenticationServiceException(
							"UserDetailsService returned null, which is an interface contract violation");
		}
		return loadedUser;
	}

	/**
	 * Sets the PasswordEncoder instance to be used to encode and validate passwords. If not set,
	 * the password will be compared as plain text.
	 * <p>
	 * For systems which are already using salted password which are encoded with a previous
	 * release, the encoder should be of type
	 * {@code org.springframework.security.authentication.encoding.PasswordEncoder} . Otherwise, the
	 * recommended approach is to use
	 * {@code org.springframework.security.crypto.password.PasswordEncoder}.
	 * 
	 * @param passwordEncoder
	 *            must be an instance of one of the {@code PasswordEncoder} types.
	 */
	public void setPasswordEncoder(Object passwordEncoder) {
		Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");

		if (passwordEncoder instanceof PasswordEncoder) {
			this.passwordEncoder = (PasswordEncoder) passwordEncoder;
			return;
		}

		if (passwordEncoder instanceof org.springframework.security.crypto.password.PasswordEncoder) {
			final org.springframework.security.crypto.password.PasswordEncoder delegate = cast(passwordEncoder);
			this.passwordEncoder = new PasswordEncoder() {
				public String encodePassword(String rawPass, Object salt) {
					checkSalt(salt);
					return delegate.encode(rawPass);
				}

				public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
					checkSalt(salt);
					return delegate.matches(rawPass, encPass);
				}

				private void checkSalt(Object salt) {
					Assert.isNull(salt, "Salt value must be null when used with crypto module PasswordEncoder");
				}
			};

			return;
		}

		throw new IllegalArgumentException("passwordEncoder must be a PasswordEncoder instance");
	}

	@SuppressWarnings("unchecked")
	private <T> T cast(Object passwordEncoder) {
		return (T) passwordEncoder;
	}

	protected PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	/**
	 * The source of salts to use when decoding passwords. <code>null</code> is a valid value,
	 * meaning the <code>DaoAuthenticationProvider</code> will present <code>null</code> to the
	 * relevant <code>PasswordEncoder</code>.
	 * <p>
	 * Instead, it is recommended that you use an encoder which uses a random salt and combines it
	 * with the password field. This is the default approach taken in the
	 * {@code org.springframework.security.crypto.password} package.
	 * 
	 * @param saltSource
	 *            to use when attempting to decode passwords via the <code>PasswordEncoder</code>
	 */
	public void setSaltSource(SaltSource saltSource) {
		this.saltSource = saltSource;
	}

	protected SaltSource getSaltSource() {
		return saltSource;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	protected UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
}
