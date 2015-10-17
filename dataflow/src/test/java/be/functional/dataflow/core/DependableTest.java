package be.functional.dataflow.core;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;

public class DependableTest {

	@Test
	public void test() {
		final Domain domain = new Domain("test");
		final IProperty<Integer> p1 = domain.newProperty(2);
		final IProperty<Integer> p2 = domain.newProperty(2);
		final IExpression<Integer> e = domain.newExpression(new Function<IExpression<?>, Integer>() {
			@Override
			public Integer apply(final IExpression<?> dep) {
				return p1.get(dep) + p2.get(dep);
			}
		});

		Assert.assertEquals(e.output(), (Integer) 5);

		p2.set(4);

		Assert.assertEquals(e.output(), (Integer) 6);
	}

}
