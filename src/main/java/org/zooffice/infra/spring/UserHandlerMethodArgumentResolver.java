
package org.zooffice.infra.spring;

import javax.servlet.http.Cookie;

import org.zooffice.model.User;
import org.zooffice.user.service.UserContext;
import org.zooffice.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * {@link HandlerMethodArgumentResolver} for {@link User} argument.
 * 
 * It passes the current user instance on {@link User} argument.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public class UserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	private UserContext userContext;

	@Autowired
	private UserService userService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#
	 * supportsParameter(org .springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(User.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#
	 * resolveArgument(org. springframework.core.MethodParameter,
	 * org.springframework.web.method.support.ModelAndViewContainer,
	 * org.springframework.web.context.request.NativeWebRequest,
	 * org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return getUserContext().getCurrentUser();
	}

	Cookie[] getCookies(NativeWebRequest webRequest) {
		return ((ServletWebRequest) webRequest).getRequest().getCookies();
	}

	/**
	 * Get current user context.<br/>
	 * This method is provided for XML based spring bean injection.
	 * 
	 * @return user context
	 */
	public UserContext getUserContext() {
		return userContext;
	}

	/**
	 * Set the current user context.<br/>
	 * This method is provided for XML based spring bean injection.
	 * 
	 * @param userContext
	 *            user context.
	 */
	public void setUserContext(UserContext userContext) {
		this.userContext = userContext;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
