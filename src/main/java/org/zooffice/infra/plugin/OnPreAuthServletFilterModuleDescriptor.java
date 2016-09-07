
package org.zooffice.infra.plugin;

import javax.servlet.Filter;

import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ContainerManagedPlugin;

/**
 * Plugin Descriptor for PreAuth Filter.
 * 
 * In the
 * {@link Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)}
 * method, <br/>
 * the plugin should set
 * {@link org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken}
 * in the {@link org.springframework.security.core.context.SecurityContext}
 * 
 * @author JunHo Yoon
 * @since 3.0.2
 */
@PluginDescriptor("on-preauth-servletfilter")
@SuppressWarnings("deprecation")
public class OnPreAuthServletFilterModuleDescriptor extends AbstractModuleDescriptor<Filter> {
	public Filter getModule() {
		return ((ContainerManagedPlugin) getPlugin()).getContainerAccessor().createBean(getModuleClass());
	}
}
