package be.functional.dataflow.core;

public interface IDependable<T> {

  T get(IDependant pDependant);
}
