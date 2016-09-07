
package org.zooffice.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zooffice.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * {@link UserDetails} implementation.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public class SecuredUser implements UserDetails {

	private static final long serialVersionUID = 9160341654874660746L;

	/**
	 * Plugin class name from which {@link User} instance is provided.
	 */
	private final String userInfoProviderClass;
	private User user;

	/**
	 * User instance used for SpringSecurity.
	 * 
	 * @param user
	 *            real user info
	 * @param userInfoProviderClass
	 *            class name who provides the user info
	 */
	public SecuredUser(User user, String userInfoProviderClass) {
		this.setUser(user);
		this.userInfoProviderClass = userInfoProviderClass;
	}

	/**
	 * Return provided authorities. It returns one Role from {@link User} in the {@link GrantedAuthority} list.
	 * 
	 * @return {@link GrantedAuthority} list
	 */
	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>(1);
		roles.add(new SimpleGrantedAuthority(getUser().getRole().getShortName()));
		return roles;
	}

	/**
	 * Return password.
	 * @return password
	 */
	@Override
	public String getPassword() {
		return getUser().getPassword();
	}

	/**
	 * Return Username (Actually user id).
	 * @return user name
	 */
	@Override
	public String getUsername() {
		return getUser().getUserId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return getUser().isEnabled();
	}

	@Override
	public boolean isAccountNonLocked() {
		return getUser().isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return getUser().isEnabled();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public String getUserInfoProviderClass() {
		return userInfoProviderClass;
	}

	/**
	 * Get auth provider class name.
	 * @return auth provider class
	 */
	public String getAuthProviderClass() {
		if (StringUtils.isNotEmpty(getUser().getAuthProviderClass())) {
			return getUser().getAuthProviderClass();
		}
		return userInfoProviderClass;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
