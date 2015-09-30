package be.functional.dataflow.core;

public interface IProperty<T> {

	T get(IExpression<?> pDependant);

	void set(T pValue);
}
