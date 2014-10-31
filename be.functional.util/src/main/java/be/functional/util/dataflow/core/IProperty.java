package be.functional.util.dataflow.core;

public interface IProperty<T> extends IDependable<T> {

  void set(T pValue);
}
