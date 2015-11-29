package be.functional.dataflow.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

import be.functional.dataflow.core.StmDomain;
import be.functional.dataflow.core.IExpression;
import be.functional.dataflow.core.IProperty;
import be.functional.dataflow.core.IValue;
import be.functional.util.event.EventImpl;
import be.functional.util.event.IEvent;
import be.functional.util.functional.SideEffect;
import be.functional.util.functional.tuple.Pair;

// Currently ignoring ModelType or ModelDescription.

public class Model implements Iterable<Pair<String,IValue<?>>> {

	private final Supplier<Map<String, IValue<?>>> _cells = Suppliers.memoize(new Supplier<Map<String, IValue<?>>>() {
		@Override public Map<String, IValue<?>> get() {
			return new HashMap<>();
		}
	});

	private final Supplier<Map<String, SideEffect>> _sideEffects = Suppliers.memoize(new Supplier<Map<String, SideEffect>>() {
		@Override public Map<String, SideEffect> get() {
			return new HashMap<>();
		}
	});

	private final StmDomain domain;

	public Model(final StmDomain domain) {
		this.domain = domain;
	}

	protected void put(final String pKey, final IValue<?> pDependable) {
		final Map<String, IValue<?>> cells = _cells.get();
		if (cells.put(pKey, pDependable) == null) {
			_eventAdded.fire(Pair.<String,IValue<?>>of(pKey, pDependable));
		}
	}

	protected void put(final String pKey, final SideEffect pSideEffect) {
		final Map<String, SideEffect> sideEffects = _sideEffects.get();
		sideEffects.put(pKey, pSideEffect);
	}

	protected <T> IExpression<IProperty<T>> path(final String[] pPropertyNames) {
		Preconditions.checkArgument(pPropertyNames.length > 0, "Expected at least one property name.");
		class Path implements Function<IExpression<?>,IProperty<T>> {
			@Override
			public IProperty<T> apply(final IExpression<?> depender) {
				final UnmodifiableIterator<String> it = Iterators.forArray(pPropertyNames);

				Model model = Model.this;
				for (;;) {
					final String next = it.next();
					if (!it.hasNext()) {
						return model.getLocalProperty(next);
					}
					final IProperty<Model> property = model.<Model>getLocalProperty(next);
					model = property.get(depender);
					if (model == null) {
						property.set(new Model(domain));
						model = property.get(depender); // the property must know that we depend on it
					}
				}
			}
			@Override
			public String toString() {
				return MoreObjects.toStringHelper("Path").addValue(Arrays.toString(pPropertyNames)).toString();
			}
		};
		return domain.newExpression(new Path());
	}

	public <T> IProperty<T> getProperty(final String... pPropertyNames) {
		Preconditions.checkArgument(pPropertyNames.length > 0, "Expected at least one property name.");
		if (pPropertyNames.length == 1) {
			return getLocalProperty(pPropertyNames[0]);
		}

		final IExpression<IProperty<T>> path = path(pPropertyNames);

		class PathProperty implements IProperty<T>, IExpression<T> {

			@Override
			public void set(final T pValue) {
				path.output().set(pValue);
			}

			@Override
			public T get(final IExpression<?> pDependant) {
				return path.get(pDependant).get(pDependant);
			}

			@Override
			public String toString() {
				return MoreObjects.toStringHelper("PathProperty").add("path", Arrays.toString(pPropertyNames)).add("expression", path).toString();
			}

			@Override
			public StmDomain getDomain() {
				return domain;
			}

			@Override
			public T output() {
				return path.output().output();
			}
		}

		return new PathProperty();
	}

	private <T> IProperty<T> getLocalProperty(final String pPropertyName) {
		final Map<String, IValue<?>> properties = _cells.get();

		@SuppressWarnings("unchecked")
		IProperty<T> property = (IProperty<T>) properties.get(pPropertyName);
		if (property == null) {
			property = domain.newProperty(null);
			properties.put(pPropertyName, property);
		}

		return property;
	}

	@Override
	public Iterator<Pair<String, IValue<?>>> iterator() {
		return Iterators.transform(_cells.get().entrySet().iterator(), new Function<Entry<String, IValue<?>>, Pair<String, IValue<?>>>() {
			@Override
			public Pair<String, IValue<?>> apply(final Entry<String, IValue<?>> pEntry) {
				return Pair.<String,IValue<?>>of(pEntry.getKey(), pEntry.getValue());
			}
		});
	}

	private final EventImpl<Pair<String, IValue<?>>> _eventAdded = new EventImpl<>();

	public IEvent<Pair<String, IValue<?>>> eventAdded() {
		return _eventAdded;
	}

	private final EventImpl<Pair<String, IValue<?>>> _eventRemoved = new EventImpl<>();

	public IEvent<Pair<String, IValue<?>>> eventRemoved() {
		return _eventRemoved;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper("Model").addValue(_cells.get()).toString();
	}

	//  public void sideEffect(IDependable<?> ) {
	//
	//  }
	//
	//  public void applySideEffects() {
	//
	//  }
}
