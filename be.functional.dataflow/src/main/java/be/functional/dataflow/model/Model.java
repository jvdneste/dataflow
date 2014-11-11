package be.functional.dataflow.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import be.functional.dataflow.core.Expression;
import be.functional.dataflow.core.IDependable;
import be.functional.dataflow.core.IProperty;
import be.functional.dataflow.core.Property;
import be.functional.util.event.EventImpl;
import be.functional.util.event.IEvent;
import be.functional.util.functional.SideEffect;
import be.functional.util.functional.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

// Currently ignoring ModelType or ModelDescription.

public class Model implements Iterable<Pair<String,IDependable<?>>> {

  private final Supplier<Map<String, IDependable<?>>> _cells = Suppliers.memoize(new Supplier<Map<String, IDependable<?>>>() {
    @Override public Map<String, IDependable<?>> get() {
      return new HashMap<>();
    }
  });

  private final Supplier<Map<String, SideEffect>> _sideEffects = Suppliers.memoize(new Supplier<Map<String, SideEffect>>() {
    @Override public Map<String, SideEffect> get() {
      return new HashMap<>();
    }
  });

  public Model() {
  }

  protected void put(final String pKey, final IDependable<?> pDependable) {
    final Map<String, IDependable<?>> cells = _cells.get();
    if (cells.put(pKey, pDependable) == null) {
      _eventAdded.fire(Pair.<String,IDependable<?>>of(pKey, pDependable));
    }
  }

  protected void put(final String pKey, final SideEffect pSideEffect) {
    final Map<String, SideEffect> sideEffects = _sideEffects.get();
    sideEffects.put(pKey, pSideEffect);
  }

  protected <T> IDependable<IProperty<T>> path(final String[] pPropertyNames) {
    Preconditions.checkArgument(pPropertyNames.length > 0, "Expected at least one property name.");
    class Path extends Expression<IProperty<T>> {
      @Override
      protected IProperty<T> calculate() {
        final UnmodifiableIterator<String> it = Iterators.forArray(pPropertyNames);

        Model model = Model.this;
        for (;;) {
          final String next = it.next();
          if (!it.hasNext()) {
            return model.getLocalProperty(next);
          }
          final IProperty<Model> property = model.<Model>getLocalProperty(next);
          model = property.get(this);
          if (model == null) {
            property.set(new Model());
            model = property.get(this); // the property must know that we depend on it
          }
        }
      }
      @Override
      public String toString() {
        return Objects.toStringHelper("Path").addValue(Arrays.toString(pPropertyNames)).toString();
      }
    };
    return new Path();
  }

  public <T> IProperty<T> getProperty(final String... pPropertyNames) {
    Preconditions.checkArgument(pPropertyNames.length > 0, "Expected at least one property name.");
    if (pPropertyNames.length == 1) {
      return getLocalProperty(pPropertyNames[0]);
    }

    final IDependable<IProperty<T>> path = path(pPropertyNames);

    class PathProperty extends Expression<T> implements IProperty<T> {

      @Override
      public void set(final T pValue) {
        path.get(this).set(pValue);
      }

      @Override
      protected T calculate() {
        return path.get(this).get(this);
      }

      @Override
      public String toString() {
        return Objects.toStringHelper("PathProperty").addValue(Arrays.toString(pPropertyNames)).toString();
      }
    }

    return new PathProperty();
  }

  private <T> IProperty<T> getLocalProperty(final String pPropertyName) {
    final Map<String, IDependable<?>> properties = _cells.get();

    @SuppressWarnings("unchecked")
    IProperty<T> property = (IProperty<T>) properties.get(pPropertyName);
    if (property == null) {
      property = Property.Create();
      properties.put(pPropertyName, property);
    }

    return property;
  }

  @Override
  public Iterator<Pair<String, IDependable<?>>> iterator() {
    return Iterators.transform(_cells.get().entrySet().iterator(), new Function<Entry<String, IDependable<?>>, Pair<String, IDependable<?>>>() {
      @Override
      public Pair<String, IDependable<?>> apply(final Entry<String, IDependable<?>> pEntry) {
        return Pair.<String,IDependable<?>>of(pEntry.getKey(), pEntry.getValue());
      }
    });
  }

  private final EventImpl<Pair<String, IDependable<?>>> _eventAdded = new EventImpl<>();

  public IEvent<Pair<String, IDependable<?>>> eventAdded() {
    return _eventAdded;
  }

  private final EventImpl<Pair<String, IDependable<?>>> _eventRemoved = new EventImpl<>();

  public IEvent<Pair<String, IDependable<?>>> eventRemoved() {
    return _eventRemoved;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper("Model").addValue(_cells.get()).toString();
  }

  //  public void sideEffect(IDependable<?> ) {
  //
  //  }
  //
  //  public void applySideEffects() {
  //
  //  }
}
