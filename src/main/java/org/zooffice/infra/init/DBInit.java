package org.zooffice.infra.init;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.zooffice.model.Role;
import org.zooffice.model.User;
import org.zooffice.security.SecuredUser;
import org.zooffice.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Database Initialization. 
 * When the first boot-up, some data(ex: user account) should be inserted into DB.
 * 
 * And... It's the perfect place to upgrade DB.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@Service
public class DBInit {
	@Autowired
	private UserRepository userRepository;

	/**
	 * Initalize DB.
	 */
	@PostConstruct
	@Transactional
	public void init() {
		createDefaultUserIfNecessary();
	}

	@Autowired
	private SaltSource saltSource;

	@Autowired
	private PasswordEncoder passwordEncoder;

	
	/**
	 * Create users.
	 * 
	 * @param userId
	 *            userId
	 * @param password
	 *            raw user password
	 * @param role
	 *            role
	 * @param userName
	 *            user name
	 * @param email
	 *            email
	 */
	private void createUser(String userId, String password, Role role, String userName, String email) {
		if (userRepository.findOneByUserId(userId) == null) {
			User user = new User();
			user.setUserId(userId);
			SecuredUser securedUser = new SecuredUser(user, null);
			Object salt = saltSource.getSalt(securedUser);
			user.setPassword(passwordEncoder.encodePassword(password, salt));
			user.setRole(role);
			user.setUserName(userName);
			user.setEmail(email);
			user.setCreatedDate(new Date());
			user = userRepository.save(user);
		}

	}

	/**
	 * Create initial users.
	 */
	private void createDefaultUserIfNecessary() {
		// If there is no users.. make admin and user and U, S, A roles.
		if (userRepository.count() < 2) {
			createUser("admin", "admin", Role.ADMIN, "admin", "admin@nhn.com");
			createUser("user", "user", Role.USER, "user", "user@nhn.com");
			createUser("superuser", "superuser", Role.SUPER_USER, "superuser", "superuser@nhn.com");
			createUser("system", "system", Role.SYSTEM_USER, "system", "system@nhn.com");
		}
	}
}
