
package org.zooffice.infra.plugin;

import org.zooffice.extension.OnControllerLifeCycleRunnable;

import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ContainerManagedPlugin;

/**
 * Plugin Descriptor for OnStartModule.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@PluginDescriptor("on-start")
@SuppressWarnings("deprecation")
public class OnControllerLifeCycleModuleDescriptor extends AbstractModuleDescriptor<OnControllerLifeCycleRunnable> {
	public OnControllerLifeCycleRunnable getModule() {
		return ((ContainerManagedPlugin) getPlugin()).getContainerAccessor().createBean(getModuleClass());
	}
}
