package org.zooffice.common.util;

import static org.zooffice.common.util.NoOp.noOp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread related utility.
 * 
 * @author JunHo Yoon
 * @since 3.0
 */
public abstract class ThreadUtil {

	private static final int RETRY_MILLISECOND = 5000;
	private static final int THREAD_WAITING_TIME = 5000;
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtil.class);

	/**
	 * Sleep in give millis without throwing exception.
	 * 
	 * @param millis
	 *            milisecond.
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LOGGER.warn(e.getMessage(), e);
		}
	}

	/**
	 * Stop thread quietly.
	 * 
	 * @param thread
	 *            thread to be stop
	 * @param stopMaessage
	 *            message to be shown when stop thread forcely
	 */
	@SuppressWarnings("deprecation")
	public static void stopQuetly(Thread thread, String stopMaessage) {
		if (thread == null) {
			return;
		}
		// Wait 5000 second for natural death.
		try {
			thread.join(THREAD_WAITING_TIME);
		} catch (Exception e) {
			// Fall through
			noOp();
		}
		try {
			thread.interrupt();
		} catch (Exception e) {
			noOp();
		}
		try {
			// Again Wait 5000 second.
			thread.join(RETRY_MILLISECOND);
		} catch (Exception e) {
			// Fall through
			noOp();
		}
		try {
			// Force to Stop
			if (thread.isAlive()) {
				LOGGER.error(stopMaessage);
				thread.stop();
			}
		} catch (Exception e) {
			// Fall through
			noOp();
		}
	}

}
