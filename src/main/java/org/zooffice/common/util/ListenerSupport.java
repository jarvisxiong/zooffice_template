package org.zooffice.common.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Generic support for listeners.
 *
 * @param <T> Listener type.
 *
 * @author Philip Aston
 */
public final class ListenerSupport<T> {

  private final List<T> m_listeners = new LinkedList<T>();

  /**
   * Add a listener.
   *
   * @param listener The listener.
   */
  public void add(T listener) {
    synchronized (m_listeners) {
      m_listeners.add(listener);
    }
  }

  /**
   * Remove all instances of the given listener.
   *
   * @param listener The listener.
   */
  public void remove(T listener) {
    synchronized (m_listeners) {
      while (m_listeners.remove(listener)) {
        // Keep checking.
      }
    }
  }

  /**
   * Adapter interface for use with
   * {@link
   * ListenerSupport#apply(net.grinder.util.ListenerSupport.Informer)}.
   */
  public interface Informer<K> {

    /**
     * Should notify the listener appropriately.
     *
     * @param listener The listener.
     */
    void inform(K listener);
  }

  /**
   * Adapter interface for use with
   * {@link
   * ListenerSupport#apply(net.grinder.util.ListenerSupport.HandlingInformer)}.
   */
  public interface HandlingInformer<K> {

    /**
     * Should notify the listener appropriately.
     *
     * @param listener
     *          The listener.
     * @return <code>true</code> => event handled, do not delegate to further
     *          Handlers.
     */
    boolean inform(K listener);
  }

  /**
   * Notify the listeners of an event.
   *
   * @param informer An adapter to be applied to each listener.
   */
  public void apply(Informer<? super T> informer) {
    for (T listener : cloneListenerList()) {
      informer.inform(listener);
    }
  }

  /**
   * Notify the listeners of an event.
   *
   * @param handler An adapter to be applied to each listener.
   * @return <code>true</code> => a listener handled the event.
   */
  public boolean apply(HandlingInformer<? super T> handler) {
    for (T listener : cloneListenerList()) {
      if (handler.inform(listener)) {
        return true;
      }
    }

    return false;
  }

  private List<T> cloneListenerList() {
    synchronized (m_listeners) {
      return new ArrayList<T>(m_listeners);
    }
  }
}
