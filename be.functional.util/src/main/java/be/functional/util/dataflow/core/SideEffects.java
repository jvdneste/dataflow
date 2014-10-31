package be.functional.util.dataflow.core;

import be.functional.util.functional.SideEffect;
import be.functional.util.functional.SideEffect1;

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
