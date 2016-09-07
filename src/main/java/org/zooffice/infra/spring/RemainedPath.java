
package org.zooffice.infra.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom Spring MVC Argument annotation which marks the arguments will store remained path of give
 * path.
 * 
 * @see RemainedPathMethodArgumentResolver
 * @author JunHo Yoon
 * @since 3.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemainedPath {
}
