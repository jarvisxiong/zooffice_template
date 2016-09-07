
package org.zooffice.infra.plugin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Plugin descriptor annotation which marks the corresponding Plugin Descriptor Key 
 * on the each {@link AbstractModuleDescriptor}.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PluginDescriptor {
	/**
	 * Plugin Descriptor Key.
	 * 
	 * @return
	 */
	String value();
}
