
package org.zooffice.infra.plugin;

import javax.servlet.Filter;

import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ContainerManagedPlugin;

/**
 * Plugin Descriptor for Servlet Filter.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@PluginDescriptor("on-servletfilter")
@SuppressWarnings("deprecation")
public class OnServletFilterModuleDescriptor extends AbstractModuleDescriptor<Filter> {
	public Filter getModule() {
		return ((ContainerManagedPlugin) getPlugin()).getContainerAccessor().createBean(getModuleClass());
	}
}
