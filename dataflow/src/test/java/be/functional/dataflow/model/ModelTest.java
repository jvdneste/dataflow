package be.functional.dataflow.model;

import org.junit.Assert;
import org.junit.Test;

import be.functional.dataflow.core.Expression;
import be.functional.dataflow.core.IDependable;
import be.functional.dataflow.core.IDependant;
import be.functional.dataflow.core.IProperty;
import be.functional.dataflow.model.Model;

public class ModelTest {

  @Test
  public void basicTest() {
    final Model model = new Model();

    final IProperty<Integer> c = model.<Integer>getProperty("a", "b", "c");
    c.set(2);

    final IProperty<Integer> d = model.<Integer>getProperty("a", "b", "d");
    d.set(3);

    final IDependable<Integer> e = new Expression<Integer>() {
      @Override
      protected Integer calculate() {
        return c.get(this) + d.get(this);
      }
    };

    Assert.assertEquals(e.get(IDependant.SIDE_EFFECT), (Integer) 5);

    final Model bReplacement = new Model();
    bReplacement.getProperty("c").set(7);
    bReplacement.getProperty("d").set(9);
    model.getProperty("a", "b").set(bReplacement);

    Assert.assertEquals((Integer) 7, c.get(IDependant.SIDE_EFFECT));
    Assert.assertEquals((Integer) 9, d.get(IDependant.SIDE_EFFECT));
    Assert.assertEquals((Integer) 16, e.get(IDependant.SIDE_EFFECT));
  }
}