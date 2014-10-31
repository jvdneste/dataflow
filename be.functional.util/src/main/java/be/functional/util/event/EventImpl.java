package be.functional.util.event;

import java.util.Set;

import com.google.common.collect.Sets;


public class EventImpl<T> implements IEvent<T> {

  private Set<IEventListener<T>> _listeners = null;

  private Set<IEventListener<T>> getListeners() {
    if (_listeners == null) {
      _listeners = Sets.newHashSet();
    }
    return _listeners;
  }

  @Override
  public void addListener(final IEventListener<T> pListener) {
    getListeners().add(pListener);
  }

  @Override
  public void removeListener(final IEventListener<T> pListener) {
    getListeners().remove(pListener);
  }

  public void fire(final T arg) {
    if (_listeners != null) {
      for (final IEventListener<T> listener : _listeners) {
        listener.eventFired(arg);
      }
    }
  }
}
