
package org.zooffice.infra.init;

import static org.zooffice.common.util.Preconditions.checkState;
import static org.zooffice.common.util.Preconditions.checkArgument;
import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import net.sf.ehcache.Ehcache;

import org.apache.commons.io.FileUtils;
import org.zooffice.infra.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

/**
 * Verify clustering is set up well. such as check the region is not duplicated. check if they use
 * same home.
 * 
 * @since3.2
 */
@Component
public class ClusterConfigurationVerifier {

	@Autowired
	private Config config;

	@Autowired
	private EhCacheCacheManager cacheManager;


	private Cache cache;

	/**
	 * Check cluster starting.
	 * 
	 * @throws IOException
	 *             exception
	 */
	@PostConstruct
	public void verifyCluster() throws IOException {
		if (config.isCluster() && !config.isTestMode()) {
			checkExHome();
			checkUsedDB();
		}
	}

	/**
	 * check if they use same home.
	 * 
	 * @throws IOException
	 *             exception
	 */
	private void checkExHome() throws IOException {
		File system = config.getHome().getSubFile("system.conf");
		checkArgument(system.exists(), "File does not exist: %s", system);
		String homeFileStamp = String.valueOf(FileUtils.checksumCRC32(system));
		cache = cacheManager.getCache("controller_home");
		for (Object eachKey : ((Ehcache) (cache.getNativeCache())).getKeys()) {
			ValueWrapper valueWrapper = cache.get(eachKey);
			if (valueWrapper != null && valueWrapper.get() != null) {
				checkState(homeFileStamp.equals(valueWrapper.get()),
								"Controller's {NGRINDER_HOME} conflict with other controller, "
												+ "Please check if you use same ngrinder home folder"
												+ " for each clustered controller !");
			}
		}
	}

	/**
	 * check if they use CUBRID in cluster mode.
	 */
	private void checkUsedDB() {
		String db = config.getDatabaseProperties().getProperty("database", "NONE").toLowerCase();
		checkState("cubrid".equals(db), "%s is unable to be used in cluster mode and Please use CUBRID !", db);
	}

}
