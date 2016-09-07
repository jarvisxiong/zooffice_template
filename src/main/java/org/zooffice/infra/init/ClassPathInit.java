
package org.zooffice.infra.init;

import java.io.File;
import java.util.Collection;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Initialize Classpath initialization for class filtering .
 * 
 * This class is used to prevent javaagent abnormal behavior of grinder agent. grinder agent is run
 * with java agent name grinder-dcr-agent-**.jar but grinder agent can mistakenly take the
 * grinder-dcr-agent-javadoc and sources file as javaagent. So.. This class deletes out the sources
 * and javadoc files of grinder-dcr-agent existing in class path.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
@Component
public class ClassPathInit {

	/**
	 * Clean up grinder-dcr-agent javadoc and source.
	 */
	@PostConstruct
	public void init() {
		final String systemClasspath = System.getProperty("java.class.path", StringUtils.EMPTY);
		for (String pathEntry : systemClasspath.split(File.pathSeparator)) {
			final File f = new File(pathEntry).getParentFile();
			final File parentFile = f != null ? f : new File(".");

			String[] exts = new String[] { "jar" };
			final Collection<File> childrenFileList = FileUtils.listFiles(parentFile, exts, false);
			for (File candidate : childrenFileList) {
				final String name = candidate.getName();
				if (name.startsWith("grinder-dcr-agent") && (name.contains("javadoc") || name.contains("source"))) {
					candidate.delete();
				}
			}
		}
	}
}
