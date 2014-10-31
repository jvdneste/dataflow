package be.functional.util.dataflow.core;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

public abstract class AbstractDependable<T> implements IDependable<T> {

  private final Set<IDependant> _dependants = Sets.newSetFromMap(new MapMaker().weakKeys().<IDependant, Boolean> makeMap());

  protected AbstractDependable() {
  }

  protected void invalidateDependants() {
    for (final IDependant dep : _dependants) {
      dep.invalidate();
    }
    _dependants.clear();
  }

  /**
   * Get the value and register the dependent.
   *
   * @param requester
   *            The requesting cell, can be null
   * @return The cell's value
   */
  @Override
  public final T get(final IDependant pDependant) {
    Preconditions.checkNotNull(pDependant);
    if (pDependant != IDependant.SIDE_EFFECT) {
      _dependants.add(pDependant);
    }
    return getValue();
  }

  protected abstract T getValue();
}
