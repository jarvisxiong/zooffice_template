package org.zooffice.common.util;

/**
 * <p>
 * Like {@link Runnable}, but guarantees that {@link #interruptibleRun()} will
 * be cleanly exited if the thread is interrupted.
 * </p>
 *
 * @author Philip Aston
 * @see net.grinder.common.UncheckedInterruptedException For policy on handling
 *  interrupted threads.
 */
public interface InterruptibleRunnable {

  /**
   * A run method that guarantees to exit if the thread is interrupted,
   * perhaps by throwing {@link
   * net.grinder.common.UncheckedInterruptedException}.
   */
  void interruptibleRun();
}
