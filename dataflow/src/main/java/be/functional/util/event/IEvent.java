package be.functional.util.event;

public interface IEvent<T> {

  void addListener(IEventListener<T> listener);
  void removeListener(IEventListener<T> listener);
}
