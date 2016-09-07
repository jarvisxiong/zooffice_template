
package org.zooffice.infra.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;

/**
 * Spring component annotation to mark this component is only necessary to
 * be created in unit test context. This annotation is mainly used to block the controller
 * creation in runtime.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface TestOnlyController {

	/**
	 * Get spring component id.
	 */
	String value() default "";
}