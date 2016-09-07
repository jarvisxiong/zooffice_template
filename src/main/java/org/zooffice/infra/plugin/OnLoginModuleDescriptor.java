
package org.zooffice.infra.plugin;

import org.zooffice.extension.OnLoginRunnable;

import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ContainerManagedPlugin;

/**
 * Plugin Descriptor for {@link OnLoginRunnable}.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */

@PluginDescriptor("on-login")
@SuppressWarnings("deprecation")
public class OnLoginModuleDescriptor extends AbstractModuleDescriptor<OnLoginRunnable> {
	/* (non-Javadoc)
	 * @see com.atlassian.plugin.descriptors.AbstractModuleDescriptor#getModule()
	 */
	public OnLoginRunnable getModule() {
		return ((ContainerManagedPlugin) getPlugin()).getContainerAccessor().createBean(getModuleClass());
	}
}
