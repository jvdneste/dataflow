package be.functional.dataflow.core;

import javax.annotation.Nullable;

public abstract class Expression<T> extends AbstractDependable<T> implements IDependant {

  private volatile boolean mIsUpToDate = false;

  @Nullable private T mValue;

  @Override
  public void invalidate() {
    mValue = null;
    mIsUpToDate = false;
    invalidateDependants();
  }

  @Override
  protected T getValue() {
    if (!mIsUpToDate) {
      mValue = calculate();
      mIsUpToDate = true;
    }
    return mValue;
  }

  protected boolean isUpToDate() {
    return mIsUpToDate;
  }

  protected abstract T calculate();
}
