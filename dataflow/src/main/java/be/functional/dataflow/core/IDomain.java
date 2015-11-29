package be.functional.dataflow.core;

import java.lang.ref.WeakReference;

public interface IDomain {

	String getName();

	void signal(Iterable<WeakReference<IDependent>> expressions);

}
