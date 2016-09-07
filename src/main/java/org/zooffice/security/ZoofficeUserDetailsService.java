
package org.zooffice.security;

import static org.zooffice.common.util.Preconditions.checkNotEmpty;

import org.zooffice.extension.OnLoginRunnable;
import org.zooffice.infra.plugin.PluginManager;
import org.zooffice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *  Customized {@link UserDetailsService}.
 * 
 * This resolve user
 * 
 * @author JunHo Yoon
 * 
 */
@Service("zoofficeUserDetailsService")
public class ZoofficeUserDetailsService implements UserDetailsService {

	protected static final Logger LOG = LoggerFactory.getLogger(ZoofficeUserDetailsService.class);

	@Autowired
	private PluginManager pluginManager;

	@Autowired
	private DefaultLoginPlugin defaultPlugin;

	@Override
	public UserDetails loadUserByUsername(String userId) {
		for (OnLoginRunnable each : getPluginManager().getEnabledModulesByClass(OnLoginRunnable.class, defaultPlugin)) {
			try {
				User user = each.loadUser(userId);
				if (user != null) {
					checkNotEmpty(user.getUserId(), "User info's userId provided by " + each.getClass().getName()
							+ " should not be empty");
					checkNotEmpty(user.getUserName(), "User info's userName provided by " + each.getClass().getName()
							+ " should not be empty");
					checkNotEmpty(user.getEmail(), "User info's email provided by " + each.getClass().getName()
							+ " should not be empty");
					return new SecuredUser(user, user.getAuthProviderClass());
				}
			} catch (NullPointerException e) {
				LOG.error("User Info retrieval is failed", e);
			}
		}
		throw new UsernameNotFoundException(userId + " is not found.");
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
}
