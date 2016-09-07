package org.zooffice.user.repository;

import java.util.List;

import org.zooffice.model.Role;
import org.zooffice.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * User repository.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	/**
	 * Find all {@link User}s based on the given spec.
	 * 
	 * @param spec
	 *            spec
	 * @return found {@link User} list
	 */
	public List<User> findAll(Specification<User> spec);

	/**
	 * Find all {@link User}s for the given role.
	 * 
	 * @param role
	 *            role
	 * @return found {@link User} list
	 */
	public List<User> findAllByRole(Role role);
	
	/**
	 * Find all {@link User}s for the given role.
	 * 
	 * @param role
	 *            role
	 * @param sort sort
	 * @return found {@link User} list
	 */
	public List<User> findAllByRole(Role role, Sort sort);

	/**
	 * Delete user which has the given userId.
	 * 
	 * @param userId
	 *            user id
	 */
	@Modifying
	@Query("delete from User u where u.userId = :userId")
	public void deleteByUserId(@Param("userId") String userId);

	/**
	 * Find one {@link User} by the given userId.
	 * 
	 * @param userId
	 *            user id
	 * @return found {@link User}. null if not found.
	 */
	public User findOneByUserId(String userId);

	/**
	 * Find one {@link User} by the given userName.
	 * 
	 * @param userName
	 *            user name
	 * @return found {@link User}. null if not found.
	 */
	public User findOneByUserName(String userName);


}
