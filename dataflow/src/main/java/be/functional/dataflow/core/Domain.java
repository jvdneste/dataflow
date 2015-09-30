package be.functional.dataflow.core;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class Domain {

	private final String name;

	public Domain(final String name) {
		this.name = name;
	}

	private volatile Thread domainThread = null;

	private class DomainThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(final Runnable r) {
			final Thread result = domainThread = new Thread(r, "Domain thread for \"" + name + "\"");
			return result;
		}
	}

	private final ExecutorService executor = Executors.newSingleThreadExecutor(new DomainThreadFactory());

	private <T> T invoke(final Callable<T> callable) {
		try {
			return executor.submit(callable).get();
		} catch (final Exception e) {
			throw new RuntimeException("callable execution failed", e);
		}
	}

	public <T> IProperty<T> newProperty(final T value) {
		return new Property<T>(value);
	}

	public <T> IExpression<T> newExpression(final Function<IExpression<?>, T> f) {
		return new Expression<T>(f);
	}

	private <T> T get(final Property<T> property, final IExpression<?> pDepender) {
		if (Thread.currentThread() == domainThread) {
			return getImpl(property, pDepender);
		}
		return invoke(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return getImpl(property, pDepender);
			}
		});
	}

	private <T> T getImpl(final Property<T> value, final IExpression<?> pDepender) {
		value.mDependers.add(new WeakReference<IExpression<?>>(pDepender));
		return value.mValue;
	}

	private <T> void set(final Property<T> property, final T value) {
		if (Thread.currentThread() == domainThread) {
			setImpl(property, value);
		} else {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					setImpl(property, value);
				}
			});
		}
	}

	private <T> void setImpl(final Property<T> property, final T value) {
		if (Objects.equal(property.mValue, value)) {
			return;
		}
		property.mValue = value;
		invalidate(property.mDependers);
	}

	private <T> T get(final Expression<T> expression, final IExpression<?> pDepender) {
		return invoke(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return getImpl(expression, pDepender);
			}
		});
	}

	private <T> T getImpl(final Expression<T> expression, final IExpression<?> pDepender) {
		if (!expression.mIsUpToDate) {
			expression.mValue = expression.mUpdate.apply(pDepender);
			expression.mIsUpToDate = true;
		}
		return expression.mValue;
	}

	public void invalidate(final Iterable<WeakReference<IExpression<?>>> dependers) {
		if (Thread.currentThread() == domainThread) {
			invalidateImpl(dependers);
		} else {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					invalidateImpl(dependers);
				}
			});
		}
	}

	private void invalidateImpl(final Iterable<WeakReference<IExpression<?>>> dependers) {
		final Multimap<Domain,WeakReference<IExpression<?>>> postponed = ArrayListMultimap.create();
		final Queue<WeakReference<IExpression<?>>> queue = Lists.newLinkedList(dependers);
		while (!queue.isEmpty()) {
			final WeakReference<IExpression<?>> currentReference = queue.poll();
			if (currentReference == null) {
				break;
			}
			final IExpression<?> current = currentReference.get();
			if (current == null) {
				continue;
			}

			final Domain currentDomain = current.getDomain();
			if (currentDomain != this) {
				postponed.put(currentDomain, currentReference);
			} else {
				final Expression<?> expression = (Expression<?>) current;
				expression.mValue = null;
				expression.mIsUpToDate = false;
				queue.addAll(expression.mDependers);
				expression.mDependers.clear();
			}
		}
		for (final Entry<Domain, Collection<WeakReference<IExpression<?>>>> entry : postponed.asMap().entrySet()) {
			final Domain domain = entry.getKey();
			final Collection<WeakReference<IExpression<?>>> expressions = entry.getValue();
			domain.invalidate(expressions);
		}
	}

	private class Property<T> implements IProperty<T> {

		public T mValue = null;

		public final List<WeakReference<IExpression<?>>> mDependers = Lists.newArrayList();

		public Property(final T pValue) {
			mValue = pValue;
		}

		@Override
		public final T get(final IExpression<?> pDepender) {
			Preconditions.checkNotNull(pDepender);
			return Domain.this.get(this, pDepender);
		}

		@Override
		public void set(final T pValue) {
			Domain.this.set(this, pValue);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper("Property").add("value", mValue).toString();
		}
	}

	private class Expression<T> implements IExpression<T> {

		public final List<WeakReference<IExpression<?>>> mDependers = Lists.newArrayList();

		public volatile boolean mIsUpToDate = false;

		public @Nullable T mValue;

		public final Function<IExpression<?>, T> mUpdate;

		public Expression(final Function<IExpression<?>, T> f) {
			this.mUpdate = f;
		}

		@Override
		public T get(final IExpression<?> pDepender) {
			Preconditions.checkNotNull(pDepender);
			return Domain.this.get(this, pDepender);
		}

		@Override
		public Domain getDomain() {
			return Domain.this;
		}
	}
}
