
package org.zooffice.infra.spring;

import org.zooffice.common.util.PathUtil;
import org.springframework.core.MethodParameter;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Custom argument resolver to catch the unresolved remaining path.
 * 
 * <pre>
 *  @RequestMapping("hello/**")
 * 	public String handleURL(@RemainedPath String path) {
 *   ....
 * 	}
 * </pre>
 * 
 * When hello/world/1 url is called, world/1 will be provided in path.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public class RemainedPathMethodArgumentResolver implements HandlerMethodArgumentResolver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org
	 * .springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(RemainedPath.class) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * 
	 * @see
	 * org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.
	 * springframework.core.MethodParameter,
	 * org.springframework.web.method.support.ModelAndViewContainer,
	 * org.springframework.web.context.request.NativeWebRequest,
	 * org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		AntPathMatcher pathMatcher = new AntPathMatcher();
		RequestMapping requestMappingOnMethod = parameter.getMethodAnnotation(RequestMapping.class);
		RequestMapping requestMappingOnClass = getDeclaringClassRequestMapping(parameter);
		String combine = pathMatcher.combine(requestMappingOnClass.value()[0], requestMappingOnMethod.value()[0]);
		return PathUtil.removePrependedSlash(pathMatcher.extractPathWithinPattern(combine, (String) webRequest
						.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE,
										NativeWebRequest.SCOPE_REQUEST)));
	}

	/**
	 * Get the request mapping annotation on the given parameter.
	 * 
	 * @param parameter
	 *            parameter
	 * @return {@link RequestMapping} annotation
	 */
	@SuppressWarnings("unchecked")
	protected RequestMapping getDeclaringClassRequestMapping(MethodParameter parameter) {
		return (RequestMapping) parameter.getDeclaringClass().getAnnotation(RequestMapping.class);
	}
}
