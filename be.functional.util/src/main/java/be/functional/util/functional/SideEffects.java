package be.functional.util.functional;

import be.functional.dataflow.core.IDependant;

public class SideEffects {

  private SideEffects() {
  }

  public static <T> SideEffect Perform(final SideEffect1<IDependant> pSideEffect) {
    class SideEffectOfDependant implements IDependant, SideEffect {
      @Override
      public void invalidate() {
        apply();
      }
      @Override
      public void apply() {
        pSideEffect.apply(this);
      }
    };
    final SideEffectOfDependant result = new SideEffectOfDependant();
    result.apply();
    return result;
  }
}
