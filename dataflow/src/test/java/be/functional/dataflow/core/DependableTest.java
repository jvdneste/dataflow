package be.functional.dataflow.core;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;

public class DependableTest {

	@Test
	public void test1() {
		final StmDomain domain = new StmDomain("test");
		final IProperty<Integer> p1 = domain.newProperty(2);
		final IProperty<Integer> p2 = domain.newProperty(2);
		final IExpression<Integer> e = domain.newExpression(new Function<IDependent, Integer>() {
			@Override
			public Integer apply(final IDependent dep) {
				return p1.get(dep) + p2.get(dep);
			}
		});

		Assert.assertEquals((Integer) 4, e.output());

		p2.set(4);

		Assert.assertEquals((Integer) 6, e.output());
	}

	@Test
	public void test2() {
		final StmDomain domain = new StmDomain("test");
		final IProperty<Integer> p1 = domain.newProperty(2);
		final IProperty<Integer> p2 = domain.newProperty(2);
		final IExpression<Integer> e1 = domain.newExpression(new Function<IDependent, Integer>() {
			@Override
			public Integer apply(final IDependent dep) {
				return p1.get(dep) + p2.get(dep);
			}
		});

		final IProperty<Integer> p3 = domain.newProperty(2);
		final IExpression<Integer> e2 = domain.newExpression(new Function<IDependent, Integer>() {
			@Override
			public Integer apply(final IDependent dep) {
				return e1.get(dep) + p3.get(dep);
			}
		});

		Assert.assertEquals((Integer) 6, e2.output());

		p1.set(3);
		Assert.assertEquals((Integer) 7, e2.output());

		p2.set(3);
		Assert.assertEquals((Integer) 8, e2.output());

		p3.set(3);
		Assert.assertEquals((Integer) 9, e2.output());
	}

	@Test
	public void test3() {
		final StmDomain domain = new StmDomain("test");
		final IProperty<Integer> p1 = domain.newProperty(2);
		final IProperty<Integer> p2 = domain.newProperty(2);
		final IExpression<Integer> e1 = domain.newExpression(new Function<IDependent, Integer>() {
			@Override
			public Integer apply(final IDependent dep) {
				return p1.get(dep) + p2.get(dep);
			}
		});

		final IProperty<Integer> p3 = domain.newProperty(2);
		final IExpression<Integer> e2 = domain.newExpression(new Function<IDependent, Integer>() {
			@Override
			public Integer apply(final IDependent dep) {
				return e1.get(dep) + p3.get(dep);
			}
		});

		final IExpression<Integer> e3 = domain.newExpression(new Function<IDependent, Integer>() {
			@Override
			public Integer apply(final IDependent dep) {
				return e2.get(dep) + e1.get(dep);
			}
		});

		Assert.assertEquals((Integer) 10, e3.output());

		p1.set(3);
		Assert.assertEquals((Integer) 12, e3.output());

		p2.set(3);
		Assert.assertEquals((Integer) 14, e3.output());

		p3.set(3);
		Assert.assertEquals((Integer) 15, e3.output());
	}
}
