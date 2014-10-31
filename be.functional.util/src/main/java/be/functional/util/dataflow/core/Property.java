package be.functional.util.dataflow.core;

import com.google.common.base.Objects;

public final class Property<T> extends AbstractDependable<T> implements IProperty<T> {
  private T mValue = null;

  private Property(final T value) {
    mValue = value;
  }

  public static <T> Property<T> Create(final T value) {
    return new Property<T>(value);
  }

  public static <T> Property<T> Create() {
    return Create(null);
  }

  @Override
  protected T getValue() {
    return mValue;
  }

  @Override
  public void set(final T value) {
    if (Objects.equal(mValue, value)) {
      return;
    }
    mValue = value;
    invalidateDependants();
  }

  @Override
  public String toString() {
    return Objects.toStringHelper("Property").add("value", mValue).toString();
  }
}
