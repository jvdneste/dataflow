package be.functional.util.dataflow.core;

public interface IDependable<T> {

  T get(IDependant pDependant);
}
