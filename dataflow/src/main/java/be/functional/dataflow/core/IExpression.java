package be.functional.dataflow.core;

public interface IExpression<T> {

	Domain getDomain();

	T get(IExpression<?> pDependant);
}
