
package org.zooffice.infra.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Spring component annotation to mark this component is only necessary to
 * be created in unit test context. This annotation is mainly used to block the component
 * creation in runtime.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface TestOnlyComponent {

	/**
	 * Get spring component id.
	 */
	String value() default "";
}