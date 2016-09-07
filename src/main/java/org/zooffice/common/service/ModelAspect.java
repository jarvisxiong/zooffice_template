package org.zooffice.common.service;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zooffice.infra.spring.SpringContext;
import org.zooffice.model.BaseModel;
import org.zooffice.model.User;
import org.zooffice.user.repository.UserRepository;
import org.zooffice.user.service.UserContext;

/**
 * Model aspect.
 * 
 * @author Liu Zhifei
 * @author JunHo Yoon
 * @since 3.0
 */
@Aspect
@Service
public class ModelAspect {

	public static final String EXECUTION_SAVE = "execution(* org.zooffice.**.*Service.save*(..))";

	@Autowired
	private UserContext userContext;

	@Autowired
	private SpringContext springContext;

	@Autowired
	private UserRepository userRepository;


	/**
	 * Save current user to modified date or created date. It's only workable when it's from servlet
	 * context.
	 * 
	 * @param joinPoint
	 *            joint point
	 */
	@Before(EXECUTION_SAVE)
	public void beforeSave(JoinPoint joinPoint) {
		for (Object object : joinPoint.getArgs()) {
			// If the object is base model and it's on request of servlet
			// It's not executed on Task scheduling.
			SpringContext springContext = getSpringContext();
			if (object instanceof BaseModel
							&& (springContext.isServletRequestContext() || springContext.isUnitTestContext())) {
				BaseModel<?> model = (BaseModel<?>) object;
				Date lastModifiedDate = new Date();
				model.setLastModifiedDate(lastModifiedDate);
				User currentUser = userContext.getCurrentUser();
				model.setLastModifiedUser(currentUser);

				if (!model.exist()) {
					model.setCreatedDate(lastModifiedDate);
					model.setCreatedUser(currentUser);
				}
			}
		}
	}

	public SpringContext getSpringContext() {
		return springContext;
	}

	public void setSpringContext(SpringContext springContext) {
		this.springContext = springContext;
	}

}
