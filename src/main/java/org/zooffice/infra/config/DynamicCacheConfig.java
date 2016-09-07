package org.zooffice.infra.config;

import static org.zooffice.common.util.TypeConvertUtil.cast;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.CacheEventListenerFactoryConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.ConfigurationFactory;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.distribution.RMICacheManagerPeerListenerFactory;
import net.sf.ehcache.distribution.RMICacheManagerPeerProviderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.zooffice.common.constant.ZoofficeConstants;
import org.zooffice.infra.logger.CoreLogger;

import com.google.common.net.InetAddresses;

/**
 * Dynamic cache configuration. This get the control of EhCache configuration from Spring. Depending
 * on the system.conf, it creates local cache or dist cache.
 * 
 * @author Mavlarn
 * @author JunHo Yoon
 * @since 3.1
 */
@Component
public class DynamicCacheConfig {

	@Autowired
	private Config config;

	/**
	 * Create cache manager dynamically according to the configuration. Because we cann't add a
	 * cluster peer provider dynamically.
	 * 
	 * 
	 * @return EhCacheCacheManager bean
	 */
	@SuppressWarnings("rawtypes")
	@Bean(name = "cacheManager")
	public EhCacheCacheManager dynamicCacheManager() {
		EhCacheCacheManager cacheManager = new EhCacheCacheManager();
		Configuration cacheManagerConfig;
		InputStream inputStream = null;
		try {
			if (!config.isCluster()) {
				CoreLogger.LOGGER.info("In no cluster mode.");
				inputStream = new ClassPathResource("ehcache.xml").getInputStream();
				cacheManagerConfig = ConfigurationFactory.parseConfiguration(inputStream);
			} else {
				CoreLogger.LOGGER.info("In cluster mode.");
				inputStream = new ClassPathResource("ehcache-dist.xml").getInputStream();
				cacheManagerConfig = ConfigurationFactory.parseConfiguration(inputStream);

				FactoryConfiguration peerProviderConfig = new FactoryConfiguration();
				peerProviderConfig.setClass(RMICacheManagerPeerProviderFactory.class.getName());
				List<String> replicatedCacheNames = getReplicatedCacheNames(cacheManagerConfig);
				String properties = createCacheProperties(replicatedCacheNames);
				peerProviderConfig.setProperties(properties);
				cacheManagerConfig.addCacheManagerPeerProviderFactory(peerProviderConfig);

				FactoryConfiguration peerListenerConfig = new FactoryConfiguration();
				peerListenerConfig.setClass(RMICacheManagerPeerListenerFactory.class.getName());
				String peerListenerProperties = createPearListenerProperties(replicatedCacheNames);
				peerListenerConfig.setProperties(peerListenerProperties);
				cacheManagerConfig.addCacheManagerPeerListenerFactory(peerListenerConfig);
				CoreLogger.LOGGER.info("clusterURLs:{}", peerListenerProperties);
			}
			cacheManagerConfig.setName(getCacheName());
			CacheManager mgr = CacheManager.create(cacheManagerConfig);
			setCacheManager(mgr);
			cacheManager.setCacheManager(mgr);
			
		} catch (IOException e) {
			CoreLogger.LOGGER.error("Error while setting up cache", e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		return cacheManager;
	}

	protected String createPearListenerProperties(List<String> replicatedCacheNames) {
		int clusterListenerPort = getCacheListenerPort();
		String currentIP = config.getCurrentIP();
		return String.format("hostName=%s, port=%d, socketTimeoutMillis=200", currentIP, clusterListenerPort);
	}

	void setCacheManager(CacheManager mgr) {
		// Do nothing.
	}

	String getCacheName() {
		return "cacheManager";
	}

	protected String createCacheProperties(List<String> replicatedCacheNames) {
		int clusterListenerPort = getCacheListenerPort();
		// rmiUrls=//10.34.223.148:40003/distributed_map|//10.34.63.28:40003/distributed_map
		List<String> uris = new ArrayList<String>();
		String currentIP = config.getCurrentIP();
		String current = currentIP + ":" + getCacheListenerPort();
		for (String ip : config.getClusterURIs()) {
			if (ip.equals(current)) {
				continue;
			}
			// Verify it's ip.
			String[] split = StringUtils.split(ip, ":");
			ip = split[0];
			int port = (split.length >= 2) ? NumberUtils.toInt(split[1], clusterListenerPort) : clusterListenerPort;
			if (!InetAddresses.isInetAddress(ip)) {
				try {
					ip = InetAddress.getByName(ip).getHostAddress();
				} catch (UnknownHostException e) {
					CoreLogger.LOGGER.error("{} is not valid ip or host name", ip);
					continue;
				}
			}

			for (String cacheName : replicatedCacheNames) {
				uris.add(String.format("//%s:%d/%s", ip, port, cacheName));
			}
		}
		return "peerDiscovery=manual,rmiUrls=" + StringUtils.join(uris, "|");
	}

	int getCacheListenerPort() {
		return config.getSystemProperties().getPropertyInt(ZoofficeConstants.ZOOFFICE_CLUSTER_LISTENER_PORT,
						Config.NGRINDER_DEFAULT_CLUSTER_LISTENER_PORT);
	}

	protected List<String> getReplicatedCacheNames(Configuration cacheManagerConfig) {
		Map<String, CacheConfiguration> cacheConfigurations = cacheManagerConfig.getCacheConfigurations();
		List<String> replicatedCacheNames = new ArrayList<String>();
		for (Map.Entry<String, CacheConfiguration> eachConfig : cacheConfigurations.entrySet()) {
			List<CacheEventListenerFactoryConfiguration> list = cast(eachConfig.getValue()
							.getCacheEventListenerConfigurations());
			for (CacheEventListenerFactoryConfiguration each : list) {
				if (each.getFullyQualifiedClassPath().equals("net.sf.ehcache.distribution.RMICacheReplicatorFactory")) {
					replicatedCacheNames.add(eachConfig.getKey());
				}
			}
		}
		return replicatedCacheNames;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
