package be.functional.dataflow.core;

public interface IValue<T> {

	/** get the value as part of an depender update */
	T get(IExpression<?> pDependant);

	/** side effect */
	T output();
}
