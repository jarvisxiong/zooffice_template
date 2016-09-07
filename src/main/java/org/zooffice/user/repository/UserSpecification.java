package org.zooffice.user.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.zooffice.model.User;
import org.springframework.data.jpa.domain.Specification;

/**
 * User entity Specification Holder.
 * 
 * @author JunHo Yoon
 */
public abstract class UserSpecification {

	private UserSpecification() {
	}

	/**
	 * Create "Name Like" query spec.
	 * @param query query
	 * @return created spec
	 */
	public static Specification<User> nameLike(final String query) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
				String pattern = ("%" + query + "%").toLowerCase();
				return cb.or(cb.like(cb.lower(root.get("userName").as(String.class)), pattern), 
						cb.like(cb.lower(root.get("userId").as(String.class)), pattern));
			}
		};
	}

	/**
	 * Create "Email like" query spec.
	 * @param query query
	 * @return created spec
	 */
	public static Specification<User> emailLike(final String query) {
		return new Specification<User>() {
			@Override
			public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
				String pattern = ("%" + query + "%").toLowerCase();
				return cb.like(cb.lower(root.get("email").as(String.class)), pattern);
			}
		};
	}

}
