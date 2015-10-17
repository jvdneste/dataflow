package be.functional.util.functional;

public class SideEffects {

	private SideEffects() {
	}

	//  public static <T> SideEffect Perform(final SideEffect1<IExpression> pSideEffect) {
	//    class SideEffectOfDependant implements IExpression, SideEffect {
	//      @Override
	//      public void invalidate() {
	//        apply();
	//      }
	//      @Override
	//      public void apply() {
	//        pSideEffect.apply(this);
	//      }
	//    };
	//    final SideEffectOfDependant result = new SideEffectOfDependant();
	//    result.apply();
	//    return result;
	//  }
}
