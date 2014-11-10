package be.functional.dataflow.core;

public interface IDependant {
  void invalidate();

  public static final IDependant SIDE_EFFECT = new IDependant() {
    @Override
    public void invalidate() {
    }
  };
}
