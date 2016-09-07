

package org.zooffice.infra.config;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.zooffice.common.model.Home;

/**
 * Application lifecycle listner.
 * 
 * @author JunHo Yoon
 * @since 3.1
 */
@Service
public class ApplicationListenerBean implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private Config config;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context
	 * .ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Home exHome = config.getExHome();
		if (exHome.exists()) {
			FileUtils.deleteQuietly(exHome.getSubFile("shutdown.lock"));
			FileUtils.deleteQuietly(exHome.getSubFile("no_more_test.lock"));
		}
	}
}