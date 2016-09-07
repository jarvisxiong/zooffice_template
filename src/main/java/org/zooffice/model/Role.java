package org.zooffice.model;

/**
 * Role of the User.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public enum Role {
	/**
	 * General user role who can create performance test entry.
	 */
	USER("U", "General User") {
	},
	/**
	 * Admin user role who can monitors tests.
	 */
	ADMIN("A", "Administrator") {

		/**
		 * Has admin permission or not.
		 * 
		 * @param type
		 *            permission type
		 * 
		 * @return has the permission or not
		 */
		public boolean hasPermission(Permission type) {
			switch (type) {
			default:
				return false;
			}
		}
	},
	/**
	 * Super user role who can set system settings and manage user account.
	 */
	SUPER_USER("S", "Super User") {

		/**
		 * Has super permission or not.
		 * 
		 * @param type
		 *            permission type
		 * 
		 * @return has the permission or not
		 */
		public boolean hasPermission(Permission type) {
			switch (type) {
			default:
				return false;
			}
		}
	},
	/**
	 * System user role. This is for the automatic batch.
	 */
	SYSTEM_USER("SYSTEM", "System User") {

	};

	private final String shortName;

	private final String fullName;

	/**
	 * Constructor.
	 * 
	 * @param shortName
	 *            short name of role... usually 1 sing char
	 * @param fullName
	 *            full name of role
	 */
	Role(String shortName, String fullName) {
		this.shortName = shortName;
		this.fullName = fullName;
	}

	/**
	 * Get the short name.
	 * 
	 * @return short name
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * Get full name.
	 * 
	 * @return full name
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * check this role whether has permission.
	 * 
	 * @param type
	 *            permission type
	 * 
	 * @return true if can
	 */
	public boolean hasPermission(Permission type) {
		return false;
	}
}
