package org.zooffice.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Index;

/**
 * Base Model which has following attribute.
 * <ul>
 * <li>created_user</li>
 * <li>created_date</li>
 * <li>lastmodified_user</li>
 * <li>lastmodified_date</li>
 * </ul>
 * 
 * @param <M>
 *            wrapped entity
 * @author Liu Zhifei
 * @author JunHo Yoon
 * @since 3.0
 */
@MappedSuperclass
public class BaseModel<M> extends BaseEntity<M> {

	private static final long serialVersionUID = -3876339828833595694L;

	@Column(name = "created_date", insertable = true, updatable = false)
	private Date createdDate;

	@ManyToOne
	@JoinColumn(name = "created_user", insertable = true, updatable = false)
	@Index(name = "created_user_index")
	private User createdUser;

	@Column(name = "last_modified_date", insertable = true, updatable = true)
	private Date lastModifiedDate;

	@ManyToOne
	@JoinColumn(name = "last_modified_user", insertable = true, updatable = true)
	@Index(name = "last_modified_user_index")
	private User lastModifiedUser;

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public User getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(User createdUser) {
		this.createdUser = createdUser;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public User getLastModifiedUser() {
		return lastModifiedUser;
	}

	public void setLastModifiedUser(User lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}
}
