package org.zooffice.operation.service;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.zooffice.infra.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Announcement operating service.
 * 
 * @author AlexQin
 * @since 3.1
 */
@Service
public class AnnouncementService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnnouncementService.class);

	@Autowired
	private Config config;

	/**
	 * Get announcement.conf file content.
	 * 
	 * @return file content.
	 */
	public String getAnnouncement() {
		return config.getAnnouncement();
	}

	/**
	 * Save content to announcement.conf file.
	 * 
	 * @param content
	 *            file content.
	 * @return save successfully or not.
	 */
	public boolean saveAnnouncement(String content) {
		try {
			FileUtils.writeStringToFile(config.getHome().getSubFile("announcement.conf"), content, "UTF-8");
			config.loadAnnouncement();
		} catch (IOException e) {
			LOGGER.error("Error while writing announcement file.");
			return false;
		}
		return true;
	}
}
