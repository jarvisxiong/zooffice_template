package org.zooffice.model;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which makes sure to merge on this properties always performed even the value is not
 * appropriate.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ForceMergable {

}
