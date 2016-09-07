package org.zooffice.common.model;

import static org.zooffice.common.util.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.zooffice.common.constant.ZoofficeConstants;
import org.zooffice.common.excpetion.ConfigurationException;
import org.zooffice.common.excpetion.ZoofficeRuntimeException;
import org.zooffice.common.util.EncodingUtil;

/**
 * Home class which enable you to easily access resources in Home directory.
 * 
 * @author Mavlarn
 * @since 1.0
 */
public class Home implements ZoofficeConstants {

	private final File directory;

	/**
	 * Constructor.
	 * 
	 * @param directory
	 *            home directory
	 */
	public Home(File directory) {
		this(directory, true);
	}

	/**
	 * Constructor.
	 * 
	 * @param directory
	 *            home directory
	 * @param create
	 *            create the directory if not exists
	 */
	public Home(File directory, boolean create) {
		checkNotNull(directory, "directory should not be null");
		if (create) {
			directory.mkdir();
		}
		if (directory.exists() && !directory.canWrite()) {
			throw new ConfigurationException(String.format(" zooffice home directory %s is not writable.", directory),
							null);
		}
		this.directory = directory;
	}

	/**
	 * Get home directory.
	 * 
	 * @return home directory
	 */
	public File getDirectory() {
		return directory;
	}

	/**
	 * Copy file from given location.
	 * 
	 * @param from
	 *            file location
	 * @param overwrite
	 *            overwrite
	 */
	public void copyFrom(File from, boolean overwrite) {
		// Copy missing files
		try {
			for (File file : from.listFiles()) {
				if (!(new File(directory, file.getName()).exists())) {
					FileUtils.copyFileToDirectory(file, directory);
				} else {
					File orgConf = new File(directory, "org_conf");
					orgConf.mkdirs();
					FileUtils.copyFile(file, new File(orgConf, file.getName()));
				}
			}
		} catch (IOException e) {
			throw new ZoofficeRuntimeException("Fail to copy files from " + from.getAbsolutePath(), e);
		}
	}

	/**
	 * Make sub directory on home directory.
	 * 
	 * @param subPathName
	 *            subpath name
	 */
	public void makeSubPath(String subPathName) {
		File subFile = new File(directory, subPathName);
		if (!subFile.exists()) {
			subFile.mkdir();
		}
	}

	/**
	 * Get the {@link Properties} named the given configuration file name.
	 * 
	 * @param confFileName
	 *            configuration file name
	 * @return loaded {@link Properties}
	 */
	public Properties getProperties(String confFileName) {
		try {
			File configFile = getSubFile(confFileName);
			if (configFile.exists()) {
				byte[] propByte = FileUtils.readFileToByteArray(configFile);
				String propString = EncodingUtil.getAutoDecodedString(propByte, "UTF-8");
				Properties prop = new Properties();
				prop.load(new StringReader(propString));
				return prop;
			} else {
				// default empty properties.
				return new Properties();
			}

		} catch (IOException e) {
			throw new ZoofficeRuntimeException("Fail to load property file " + confFileName, e);
		}
	}

	/**
	 * Get sub {@link File} instance under home directory.
	 * 
	 * @param subPathName
	 *            subpath name
	 * @return {@link File}
	 */
	public File getSubFile(String subPathName) {
		return new File(directory, subPathName);
	}

	/**
	 * Get the plugin directory.
	 * 
	 * @return plugin directory.
	 */
	public File getPluginsDirectory() {
		return getSubFile(PLUGIN_PATH);
	}

	/**
	 * Get global log file.
	 * 
	 * @return log file
	 */
	public File getGlobalLogFile() {
		File subFile = getSubFile(GLOBAL_LOG_PATH);
		return subFile;
	}

	/**
	 * Check the this home exists.
	 * 
	 * @return true if exists.
	 */
	public boolean exists() {
		return directory.exists();
	}

	public File getMessagesDirectory() {
		return getSubFile(MESSAGE_PATH);
	}
}
