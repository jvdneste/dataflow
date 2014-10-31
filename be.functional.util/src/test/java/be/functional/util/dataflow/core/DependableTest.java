package be.functional.util.dataflow.core;

import org.junit.Assert;
import org.junit.Test;

public class DependableTest {

  @Test
  public void test() {
    final Property<Integer> p1 = Property.Create(2);
    final Property<Integer> p2 = Property.Create(3);
    final Expression<Integer> e = new Expression<Integer>() {
      @Override
      protected Integer calculate() {
        return p1.get(this) + p2.get(this);
      }
    };

    Assert.assertEquals(e.get(IDependant.SIDE_EFFECT), (Integer) 5);

    p2.set(4);

    Assert.assertEquals(e.get(IDependant.SIDE_EFFECT), (Integer) 6);
  }

}
