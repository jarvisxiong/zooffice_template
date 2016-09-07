
package org.zooffice.infra.config;

import static org.zooffice.common.util.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.helpers.FileWatchdog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.zooffice.common.constant.ZoofficeConstants;
import org.zooffice.common.excpetion.ConfigurationException;
import org.zooffice.common.model.Home;
import org.zooffice.common.util.NetworkUtil;
import org.zooffice.common.util.PropertiesWrapper;
import org.zooffice.infra.logger.CoreLogger;
import org.zooffice.service.IConfig;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * Spring component which is responsible to get configurations which is stored
 * in ${ZOOFFICE_HOME}.
 * 
 * @author JunHo Yoon
 * @since 1.0
 */
@Component
public class Config implements IConfig, ZoofficeConstants {
	private static final String ZOOFFICE_DEFAULT_FOLDER = ".zooffice";
	private static final String ZOOFFICE_EX_FOLDER = ".zooffice_ex";

	private static final Logger LOG = LoggerFactory.getLogger(Config.class);
	private Home home = null;
	private Home exHome = null;
	private PropertiesWrapper internalProperties;
	private PropertiesWrapper systemProperties;
	private PropertiesWrapper databaseProperties;
	private String announcement;
	private static String versionString = "";
	private boolean verbose;
	private String currentIP;

	public static final int NGRINDER_DEFAULT_CLUSTER_LISTENER_PORT = 40003;

	public static final String NONE_REGION = "NONE";
	private boolean cluster;

	/**
	 * Make it singleton.
	 */
	Config() {
	}

	/**
	 * Initialize Config. This method mainly perform NGRINDER_HOME resolution and system properties
	 * load. In addition, Logger is initialized and default configuration file is copied into
	 * NGRINDER_HOME if it's the first
	 */
	@PostConstruct
	public void init() {
		try {
			CoreLogger.LOGGER.info("Zooffice is starting...");
			home = resolveHome();
			exHome = resolveExHome();
			copyDefaultConfigurationFiles();
			loadIntrenalProperties();
			loadSystemProperties();
			initHomeMonitor();
			// Load cluster in advance. cluster mode is not dynamically reloadable.
			cluster = getSystemProperties().getPropertyBoolean(ZoofficeConstants.SYSTEM_CLUSTER_MODE, false);
			initLogger(isTestMode());
			resolveLocalIp();
			loadDatabaseProperties();
			setRMIHostName();
			versionString = getVesion();
		} catch (IOException e) {
			throw new ConfigurationException("Error while init Zooffice", e);
		}
	}

	protected void resolveLocalIp() {
		currentIP = getSystemProperties().getProperty(ZOOFFICE_SERVER_ADDRESS,
						NetworkUtil.getLocalHostAddress());
	}

	/**
	 * Destroy bean.
	 */
	@PreDestroy
	public void destroy() {
		announcementWatchDog.interrupt();
		systemConfWatchDog.interrupt();
	}

	/**
	 * Set rmi server host name.
	 * 
	 * @since 3.1
	 */
	protected void setRMIHostName() {
		if (isCluster()) {
			if (getRegion().equals(NONE_REGION)) {
				LOG.error("Region is not set in cluster mode. Please set ngrinder.region properly.");
			} else {
				CoreLogger.LOGGER.info("Cache cluster URIs:{}", getClusterURIs());
				// set rmi server host for remote serving. Otherwise, maybe it
				// will use 127.0.0.1 to
				// serve.
				// then the remote client can not connect.
				CoreLogger.LOGGER.info("Set current IP:{} for RMI server.", getCurrentIP());
				System.setProperty("java.rmi.server.hostname", getCurrentIP());
			}
		}
	}

	/**
	 * Check whether the cache cluster is set.
	 * 
	 * @return true is cache cluster set
	 * @since 3.1
	 */
	public boolean isCluster() {
		return cluster;
	}

	/**
	 * Get the cluster URIs in configuration.
	 * 
	 * @return cluster uri strings
	 */
	public String[] getClusterURIs() {
		String clusterUri = getSystemProperties().getProperty(ZOOFFICE_CLUSTER_URIS, "");
		return StringUtils.split(clusterUri, ";");
	}

	/**
	 * Get the region in configuration.
	 * 
	 * @return region
	 */
	public String getRegion() {
		return isCluster() ? getSystemProperties().getProperty(ZOOFFICE_CLUSTER_REGION, NONE_REGION) : NONE_REGION;
	}


	/**
	 * Initialize Logger.
	 * 
	 * @param forceToVerbose
	 *            force to verbose logging.
	 */
	public synchronized void initLogger(boolean forceToVerbose) {
		setupLogger((forceToVerbose) ? true : getSystemProperties().getPropertyBoolean(SYSTEM_VERBOSE, false));
	}

	/**
	 * Set up logger.
	 * 
	 * @param verbose
	 *            verbose mode?
	 */
	protected void setupLogger(boolean verbose) {
		this.verbose = verbose;
		final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		final JoranConfigurator configurator = new JoranConfigurator();
		configurator.setContext(context);
		context.reset();
		context.putProperty("LOG_LEVEL", verbose ? "DEBUG" : "INFO");
		if (exHome.exists() && isCluster()) {
			context.putProperty("LOG_DIRECTORY", exHome.getGlobalLogFile().getAbsolutePath());
			context.putProperty("SUFFIX", "_" + getRegion());
		} else {
			context.putProperty("SUFFIX", "");
			context.putProperty("LOG_DIRECTORY", home.getGlobalLogFile().getAbsolutePath());
		}
		try {
			configurator.doConfigure(new ClassPathResource("/logback/logback.xml").getFile());
		} catch (JoranException e) {
			CoreLogger.LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			CoreLogger.LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Copy default files.
	 * 
	 * @throws IOException
	 *             occurs when there is no such a files.
	 */
	protected void copyDefaultConfigurationFiles() throws IOException {
		checkNotNull(home);
		home.copyFrom(new ClassPathResource("home_template").getFile(), false);
		home.makeSubPath(PLUGIN_PATH);
	}

	/**
	 * Resolve nGrinder home path.
	 * 
	 * @return resolved home
	 */
	protected Home resolveHome() {
		String userHomeFromEnv = System.getenv("ZOOFFICE_HOME");
		String userHomeFromProperty = System.getProperty("zooffice.home");
		if (!StringUtils.equals(userHomeFromEnv, userHomeFromProperty)) {
			CoreLogger.LOGGER.warn("The path to zooffice-home is ambiguous:");
			CoreLogger.LOGGER.warn("    System Environment:  ZOOFFICE_HOME=" + userHomeFromEnv);
			CoreLogger.LOGGER.warn("    Java Sytem Property:  zooffice.home=" + userHomeFromProperty);
			CoreLogger.LOGGER.warn("    '" + userHomeFromProperty + "' is accepted.");
		}
		String userHome = StringUtils.defaultIfEmpty(userHomeFromProperty, userHomeFromEnv);
		File homeDirectory = (StringUtils.isNotEmpty(userHome)) ? new File(userHome) : new File(
						System.getProperty("user.home"), ZOOFFICE_DEFAULT_FOLDER);
		CoreLogger.LOGGER.info("zooffice home directory:{}.", userHome);

		return new Home(homeDirectory);
	}

	/**
	 * Resolve nGrinder extended home path.
	 * 
	 * @return resolved home
	 */
	protected Home resolveExHome() {
		String exHomeFromEnv = System.getenv("ZOOFFICE_EX_HOME");
		String exHomeFromProperty = System.getProperty("zooffice.exhome");
		if (!StringUtils.equals(exHomeFromEnv, exHomeFromProperty)) {
			CoreLogger.LOGGER.warn("The path to zooffice-exhome is ambiguous:");
			CoreLogger.LOGGER.warn("    System Environment:  ZOOFFICE_EX_HOME=" + exHomeFromEnv);
			CoreLogger.LOGGER.warn("    Java Sytem Property:  zooffice.exhome=" + exHomeFromProperty);
			CoreLogger.LOGGER.warn("    '" + exHomeFromProperty + "' is accepted.");
		}
		String userHome = StringUtils.defaultIfEmpty(exHomeFromProperty, exHomeFromEnv);
		File exHomeDirectory = (StringUtils.isNotEmpty(userHome)) ? new File(userHome) : new File(
						System.getProperty("user.home"), ZOOFFICE_EX_FOLDER);
		CoreLogger.LOGGER.info("zooffice ex home directory:{}.", exHomeDirectory);

		return new Home(exHomeDirectory, false);
	}

	/**
	 * Load internal properties which is not modifiable by user.
	 */
	protected void loadIntrenalProperties() {
		InputStream inputStream = null;
		Properties properties = new Properties();
		try {
			inputStream = new ClassPathResource("/internal.properties").getInputStream();
			properties.load(inputStream);
			internalProperties = new PropertiesWrapper(properties);
		} catch (IOException e) {
			CoreLogger.LOGGER.error("Error while load internal.properties", e);
			internalProperties = new PropertiesWrapper(properties);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	/**
	 * Load database related properties. (database.conf)
	 * 
	 */
	protected void loadDatabaseProperties() {
		checkNotNull(home);
		Properties properties = home.getProperties("database.conf");
		properties.put("ZOOFFICE_HOME", home.getDirectory().getAbsolutePath());
		databaseProperties = new PropertiesWrapper(properties);
	}

	/**
	 * Load system related properties. (system.conf)
	 */
	public synchronized void loadSystemProperties() {
		checkNotNull(home);
		Properties properties = home.getProperties("system.conf");
		properties.put("NGRINDER_HOME", home.getDirectory().getAbsolutePath());
		// Override if exists
		if (exHome.exists()) {
			Properties exProperties = exHome.getProperties("system-ex.conf");
			properties.putAll(exProperties);
		}
		systemProperties = new PropertiesWrapper(properties);
	}

	/**
	 * Load announcement content.
	 */
	public synchronized void loadAnnouncement() {
		checkNotNull(home);
		File sysFile = home.getSubFile("announcement.conf");
		try {
			announcement = FileUtils.readFileToString(sysFile, "UTF-8");
			return;
		} catch (IOException e) {
			CoreLogger.LOGGER.error("Error while reading announcement file.", e);
			announcement = "";
		}
	}

	/** Configuration watch docs. */
	private FileWatchdog announcementWatchDog;
	private FileWatchdog systemConfWatchDog;

	private void initHomeMonitor() {
		checkNotNull(home);
		this.announcementWatchDog = new FileWatchdog(getHome().getSubFile("announcement.conf").getAbsolutePath()) {
			@Override
			protected void doOnChange() {
				CoreLogger.LOGGER.info("Announcement file changed.");
				loadAnnouncement();
			}
		};
		announcementWatchDog.setName("WatchDog - annoucenment.conf");
		announcementWatchDog.setDelay(2000);
		announcementWatchDog.start();
		this.systemConfWatchDog = new FileWatchdog(getHome().getSubFile("system.conf").getAbsolutePath()) {
			@Override
			protected void doOnChange() {
				CoreLogger.LOGGER.info("System conf file changed.");
				loadSystemProperties();
			}
		};
		systemConfWatchDog.setName("WatchDoc - system.conf");
		systemConfWatchDog.setDelay(2000);
		systemConfWatchDog.start();
	}

	/**
	 * Get the database properties.
	 * 
	 * @return database properties
	 */
	public PropertiesWrapper getDatabaseProperties() {
		checkNotNull(databaseProperties);
		return databaseProperties;
	}

	/**
	 * Check if it's test mode.
	 * 
	 * @return true if test mode
	 */
	public boolean isTestMode() {
		return getSystemProperties().getPropertyBoolean("testmode", false);
	}

	/**
	 * Check if it's the user security enabled mode.
	 * 
	 * @return true if user security is enabled.
	 */
	public boolean isUserSecurityEnabled() {
		return getSystemProperties().getPropertyBoolean("user.security", true);
	}
	
	/**
	 * Check if it's the security enabled mode.
	 * 
	 * @return true if security is enabled.
	 */
	public boolean isSecurityEnabled() {
		return !isTestMode() && getSystemProperties().getPropertyBoolean("security", false);
	}

	/**
	 * Check if it is demo mode.
	 * 
	 * @return true if demo mode is enabled.
	 */
	public boolean isDemo() {
		return getSystemProperties().getPropertyBoolean("demo", false);
	}

	/**
	 * Check if plugin support is enabled. The reason why we need this configuration is that it
	 * takes time to initialize plugin system in unit test context.
	 * 
	 * @return true if plugin is supported.
	 */
	public boolean isPluginSupported() {
		return !isTestMode() && (getSystemProperties().getPropertyBoolean("pluginsupport", true));
	}

	/**
	 * Get the resolved home folder.
	 * 
	 * @return home
	 */
	public Home getHome() {
		return this.home;
	}

	/**
	 * Get the resolved extended home folder.
	 * 
	 * @since 3.1
	 * @return home
	 */
	public Home getExHome() {
		return this.exHome;
	}

	/**
	 * Get the system properties.
	 * 
	 * @return {@link PropertiesWrapper} which is loaded from system.conf.
	 */
	public PropertiesWrapper getSystemProperties() {
		return checkNotNull(systemProperties);
	}

	/**
	 * Get announcement content.
	 * 
	 * @return loaded from announcement.conf.
	 */
	public String getAnnouncement() {
		return announcement;
	}

	/**
	 * Get nGrinder version number.
	 * 
	 * @return nGrinder version number. If not set, return "0.0.1"
	 */
	public String getVesion() {
		return getInternalProperties().getProperty("ngrinder.version", "0.0.1");
	}

	/**
	 * Get the internal properties.
	 * 
	 * @return internal properties
	 */
	public PropertiesWrapper getInternalProperties() {
		return internalProperties;
	}

	/**
	 * Get nGrinder version in static way.
	 * 
	 * @return nGrinder version.
	 */
	public static String getVerionString() {
		return versionString;
	}

	/**
	 * Check if it's verbose logging mode.
	 * 
	 * @return true if verbose
	 */
	public boolean isVerbose() {
		return verbose;
	}

	public String getCurrentIP() {
		return currentIP;
	}

	public boolean isInvisibleRegion() {
		return getSystemProperties().getPropertyBoolean(ZOOFFICE_REGION_HIDE, false);
	}

	/**
	 * Check the no more test lock to block further test execution.
	 * 
	 * @return true if it exists
	 */
	public boolean hasNoMoreTestLock() {
		if (exHome.exists()) {
			return exHome.getSubFile("no_more_test.lock").exists();
		}
		return false;
	}

	/**
	 * Check the shutdown lock to exclude this machine from somewhere(maybe L4).
	 * 
	 * @return true if it exists
	 */
	public boolean hasShutdownLock() {
		if (exHome.exists()) {
			return exHome.getSubFile("shutdown.lock").exists();
		}
		return false;
	}

}
