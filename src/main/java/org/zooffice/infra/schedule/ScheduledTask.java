
package org.zooffice.infra.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zooffice.common.util.InterruptibleRunnable;
import org.zooffice.common.util.ListenerHelper;
import org.zooffice.common.util.ListenerSupport;
import org.zooffice.common.util.ListenerSupport.Informer;

/**
 * Convenient class which makes scheduled task.
 * 
 * @author JunHo Yoon
 * @since 3.1
 */
@Service
public class ScheduledTask {

	private ListenerSupport<InterruptibleRunnable> runListenersEvery3Sec = ListenerHelper.create();
	private ListenerSupport<InterruptibleRunnable> runListenersEvery10Sec = ListenerHelper.create();

	/**
	 * Run scheduled task with every 3 secs.
	 */
	@Scheduled(fixedDelay = 3000)
	public void doTaskOnEvery3Sec() {
		runListenersEvery3Sec.apply(new Informer<InterruptibleRunnable>() {
			@Override
			public void inform(InterruptibleRunnable listener) {
				listener.interruptibleRun();
			}
		});
	}

	/**
	 * Run scheduled task with every 10 secs.
	 */
	@Scheduled(fixedDelay = 10000)
	public void doTaskOnEvery10Sec() {
		runListenersEvery10Sec.apply(new Informer<InterruptibleRunnable>() {
			@Override
			public void inform(InterruptibleRunnable listener) {
				listener.interruptibleRun();
			}
		});
	}

	/**
	 * Add scheduled task on 3 sec scheduler.
	 * 
	 * @param runnable
	 *            runnable
	 */
	public void addScheduledTaskEvery3Sec(InterruptibleRunnable runnable) {
		runListenersEvery3Sec.add(runnable);
	}

	/**
	 * Add scheduled task on 10 sec scheduler.
	 * 
	 * @param runnable
	 *            runnable
	 */
	public void addScheduledTaskEvery5Sec(InterruptibleRunnable runnable) {
		runListenersEvery10Sec.add(runnable);
	}
}
