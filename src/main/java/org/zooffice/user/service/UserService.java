package org.zooffice.user.service;

import static org.zooffice.common.util.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zooffice.model.Role;
import org.zooffice.model.User;
import org.zooffice.security.SecuredUser;
import org.zooffice.service.IUserService;
import org.zooffice.user.repository.UserRepository;
import org.zooffice.user.repository.UserSpecification;

/**
 * The Class UserService.
 * 
 * @author Yubin Mao
 * @author AlexQin
 */
@Service
public class UserService implements IUserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SaltSource saltSource;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * get user by user id.
	 * 
	 * @param userId
	 *            user id
	 * @return user
	 */
	@Transactional
	@Cacheable("users")
	public User getUserById(String userId) {
		return userRepository.findOneByUserId(userId);
	}


	/**
	 * Encoding given user's password.
	 * 
	 * @param user
	 *            user
	 */
	public void encodePassword(User user) {
		if (StringUtils.isNotBlank(user.getPassword())) {
			SecuredUser securedUser = new SecuredUser(user, null);
			String encodePassword = passwordEncoder.encodePassword(user.getPassword(), saltSource.getSalt(securedUser));
			user.setPassword(encodePassword);
		}
	}

	/**
	 * get all users by role.
	 * 
	 * @param roleName
	 *            role name
	 * @return found user list
	 */
	public List<User> getAllUserByRole(String roleName) {
		return getAllUserByRole(roleName, new Sort(Direction.ASC, "userName"));
	}

	/**
	 * get all users by role.
	 * 
	 * @param roleName
	 *            role name
	 * @param sort
	 *            sort method
	 * @return found user list
	 */
	public List<User> getAllUserByRole(String roleName, Sort sort) {
		if (StringUtils.isBlank(roleName)) {
			return userRepository.findAll(sort);
		} else {
			return getUserListByRole(getRole(roleName), sort);
		}
	}

	/**
	 * create user.
	 * 
	 * @param user
	 *            include id, userID, fullName, role, password.
	 * 
	 * @return result
	 */
	@Transactional
	@CacheEvict(value = "users", key = "#user.userId")
	public User saveUser(User user) {
		encodePassword(user);
		User createdUser = userRepository.save(user);
		return createdUser;
	}


	/**
	 * Add user.
	 * 
	 * @param user
	 *            user
	 * @param role
	 *            role
	 */
	@CacheEvict(value = "users", key = "#user.userId")
	public void saveUser(User user, Role role) {
		encodePassword(user);
		user.setRole(role);
		userRepository.save(user);
	}

	/**
	 * modify user information.
	 * 
	 * @param user
	 *            user
	 * @param shareUserIds
	 *            It is a list of user IDs to share the permission of user
	 * @return user id
	 */
	@Transactional
	@CacheEvict(value = "users", key = "#user.userId")
	public String modifyUser(User user, String shareUserIds) {
		checkNotNull(user, "user should be not null, when modifying user");
		checkNotNull(user.getId(), "user id should be provided when modifying user");

		shareUserIds = (String) ObjectUtils.defaultIfNull(shareUserIds, "");
		List<User> newShareUsers = new ArrayList<User>();
		String[] userIds = shareUserIds.split(",");
		for (String userId : userIds) {
			User shareUser = userRepository.findOneByUserId(userId.trim());
			newShareUsers.add(shareUser);
		}
		encodePassword(user);
		User targetUser = userRepository.findOne(user.getId());
		targetUser.merge(user);
		userRepository.save(targetUser);
		return user.getUserId();
	}

	/**
	 * Delete user. All corresponding perftest and directories are deleted as well.
	 * 
	 * @param userIds
	 *            the user id string list
	 */
	@Transactional
	@CacheEvict(value = "users", allEntries = true)
	public void deleteUsers(List<String> userIds) {
		for (String userId : userIds) {
			User user = getUserById(userId);
			userRepository.delete(user);
		}
	}

	/**
	 * get the user list by the given role.
	 * 
	 * @param role
	 *            role
	 * @param sort
	 *            sort
	 * @return found user list
	 * @throws Exception
	 */
	public List<User> getUserListByRole(Role role, Sort sort) {
		return userRepository.findAllByRole(role, sort);
	}

	/**
	 * get the user list by the given role.
	 * 
	 * @param role
	 *            role
	 * @return found user list
	 * @throws Exception
	 */
	public List<User> getUserListByRole(Role role) {
		return getUserListByRole(role, new Sort(Direction.ASC, "userName"));
	}

	/**
	 * get Role object based on role name.
	 * 
	 * @param roleName
	 *            role name
	 * @return found Role
	 */
	public Role getRole(String roleName) {
		if (Role.ADMIN.getFullName().equals(roleName)) {
			return Role.ADMIN;
		} else if (Role.USER.getFullName().equals(roleName)) {
			return Role.USER;
		} else if (Role.SUPER_USER.getFullName().equals(roleName)) {
			return Role.SUPER_USER;
		} else if (Role.SYSTEM_USER.getFullName().equals(roleName)) {
			return Role.SYSTEM_USER;
		} else {
			return null;
		}
	}

	/**
	 * Get the user list by nameLike spec.
	 * 
	 * @param name
	 *            name of user
	 * @return found user list
	 */
	public List<User> getUserListByKeyWord(String name) {
		return userRepository.findAll(UserSpecification.nameLike(name));
	}

}
