package org.zooffice.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/**
 * User managed by nGrinder.
 * 
 * @author Mavlarn
 * @since 3.0
 */
@Entity
@Table(name = "NUSER")
public class User extends BaseModel<User> {

	private static final long serialVersionUID = 7398072895183814285L;

	@Column(name = "user_id", unique = true, nullable = false)
	/** User Id */
	private String userId;

	@Column(name = "user_name")
	/** User Name e.g) Jone Dogh. */
	private String userName;

	private String password;

	@Type(type = "true_false")
	@Column(columnDefinition = "char(1)")
	private boolean enabled = true;

	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "role_name", nullable = false)
	private Role role;

	private String description;

	private String timeZone;

	@Column(name = "user_language")
	private String userLanguage;

	@Column(name = "mobile_phone")
	private String mobilePhone;

	@Column(name = "is_external", columnDefinition = "char(1)")
	@Type(type = "true_false")
	private boolean external = false;

	@Column(name = "authentication_provider_class")
	/** Who provide the authentication */
	private String authProviderClass;

	/**
	 * Default constructor.
	 */
	public User() {
	}

	/**
	 * Constructor.
	 * 
	 * @param userId
	 *            user id
	 * @param name
	 *            user name
	 * @param password
	 *            password
	 * @param role
	 *            role
	 * @deprecated
	 */
	public User(String userId, String name, String password, Role role) {
		this.userId = userId;
		this.password = password;
		this.userName = name;
		this.role = role;
		isEnabled();
	}

	/**
	 * Constructor.
	 * 
	 * @param userId
	 *            user id
	 * @param name
	 *            user name
	 * @param password
	 *            password
	 * @param email
	 *            email
	 * @param role
	 *            role
	 */
	public User(String userId, String name, String password, String email, Role role) {
		this.userId = userId;
		this.password = password;
		this.userName = name;
		this.email = email;
		this.role = role;
		isEnabled();
	}

	/**
	 * Check this user is valid.
	 * 
	 * @return true if valid
	 */
	public boolean validate() {
		return !(userName == null || email == null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		return true;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	@ForceMergable
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getEmail() {
		return email;
	}

	@ForceMergable
	public void setEmail(String email) {
		this.email = email.toLowerCase();
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getUserLanguage() {
		return userLanguage;
	}

	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}

	public boolean isExternal() {
		return external;
	}

	public void setExternal(boolean external) {
		this.external = external;
	}

	public String getAuthProviderClass() {
		return authProviderClass;
	}

	public void setAuthProviderClass(String authProviderClass) {
		this.authProviderClass = authProviderClass;
	}

	/**
	 * Get user simple information.
	 * 
	 * @return user
	 */
	// It will throw StackOverflowException if return User that contains owners and followers value
	// in getCurrentPerfTestStatistics() method.so just return base User info
	public User getUserBaseInfo() {
		User userInfo = new User();
		userInfo.setId(this.getId());
		userInfo.setUserId(this.getUserId());
		userInfo.setUserName(this.getUserName());
		userInfo.setEmail(this.getEmail());

		return userInfo;
	}

	/**
	 * a string representation of User object.
	 * 
	 * @return User object information String.
	 */
	// avoid lazy initialization issues ,method toString not contain followers and owners
	@Override
	public String toString() {
		return "User[ID=" + this.getId() + ",name=" + this.getUserId() + ",Role=" + this.getRole() + "]";
	}
}
