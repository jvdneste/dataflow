package be.functional.dataflow.core;

public interface IValue<T> {

	/** get the value as part of a depender update */
	T get(IDependent pDependant);

	/** side effect */
	T output();
}
