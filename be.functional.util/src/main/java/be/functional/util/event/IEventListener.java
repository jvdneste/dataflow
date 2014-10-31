package be.functional.util.event;

public interface IEventListener<T> {

  void eventFired(final T arg);
}
