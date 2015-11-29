package be.functional.dataflow.model;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;

import be.functional.dataflow.core.StmDomain;
import be.functional.dataflow.core.IExpression;
import be.functional.dataflow.core.IProperty;
import be.functional.dataflow.core.IValue;

public class ModelTest {

	@Test
	public void basicTest1() {

		final StmDomain domain = new StmDomain("test");

		final Model model = new Model(domain);

		final IProperty<Integer> b = model.<Integer>getProperty("a", "b");
		b.set(2);

		final IProperty<Integer> c = model.<Integer>getProperty("a", "c");
		c.set(3);

		final IValue<Integer> sum = domain.newExpression(new Function<IExpression<?>, Integer>() {
			@Override
			public Integer apply(final IExpression<?> dep) {
				return b.get(dep) + c.get(dep);
			}
		});

		Assert.assertEquals(sum.output(), (Integer) 5);

		final Model replacement = new Model(domain);
		replacement.getProperty("b").set(7);
		replacement.getProperty("c").set(9);
		model.getProperty("a").set(replacement);

		Assert.assertEquals((Integer) 7, b.output());
		Assert.assertEquals((Integer) 9, c.output());
		Assert.assertEquals((Integer) 16, sum.output());
	}

	@Test
	public void basicTest2() {

		final StmDomain domain = new StmDomain("test");

		final Model model = new Model(domain);

		final IProperty<Integer> c = model.<Integer>getProperty("a", "b", "c");
		c.set(2);

		final IProperty<Integer> d = model.<Integer>getProperty("a", "b", "d");
		d.set(3);

		final IValue<Integer> e = domain.newExpression(new Function<IExpression<?>, Integer>() {
			@Override
			public Integer apply(final IExpression<?> dep) {
				return c.get(dep) + d.get(dep);
			}
		});

		Assert.assertEquals(e.output(), (Integer) 5);

		final Model bReplacement = new Model(domain);
		bReplacement.getProperty("c").set(7);
		bReplacement.getProperty("d").set(9);
		model.getProperty("a", "b").set(bReplacement);

		Assert.assertEquals((Integer) 7, c.output());
		Assert.assertEquals((Integer) 9, d.output());
		Assert.assertEquals((Integer) 16, e.output());
	}
}
