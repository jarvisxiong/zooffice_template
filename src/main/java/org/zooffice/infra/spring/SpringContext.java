
package org.zooffice.infra.spring;

import org.zooffice.infra.annotation.RuntimeOnlyComponent;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Convenient class to determine if the current runtime is in the spring context.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@RuntimeOnlyComponent
public class SpringContext {
	/**
	 * Determine if the current thread is from servlet context.
	 * 
	 * @return true if it's servlet context.
	 */
	public boolean isServletRequestContext() {
		return RequestContextHolder.getRequestAttributes() != null;
	}

	/**
	 * Determine if this context is on unit test.
	 * 
	 * @see MockSpringContext
	 * @return always false.
	 */
	public boolean isUnitTestContext() {
		return false;
	}
}
