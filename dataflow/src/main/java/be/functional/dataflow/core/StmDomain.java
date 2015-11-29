package be.functional.dataflow.core;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import scala.concurrent.stm.Ref.View;
import scala.concurrent.stm.japi.STM;

public class StmDomain implements IDomain {

	private final String name;

	public StmDomain(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public <T> IProperty<T> newProperty(final T value) {
		return new Property<T>(value);
	}

	public <T> IExpression<T> newExpression(final Function<IDependent, T> f) {
		return new Expression<T>(f);
	}

	@Override
	public void signal(final Iterable<WeakReference<IDependent>> dependents) {
		final Multimap<IDomain,WeakReference<IDependent>> postponed = ArrayListMultimap.create();
		final Queue<WeakReference<IDependent>> queue = Lists.newLinkedList(dependents);
		while (!queue.isEmpty()) {
			final WeakReference<IDependent> currentRef = queue.poll();
			if (currentRef == null) {
				break;
			}
			final IDependent current = currentRef.get();
			if (current == null) {
				continue;
			}

			final IDomain currentDomain = current.getDomain();
			if (currentDomain != this) {
				postponed.put(currentDomain, currentRef);
			} else {
				final Expression<?> expression = (Expression<?>) current;
				expression.mValue.set(null);
				expression.mIsUpToDate.set(false);
				for (final WeakKey key : expression.mDependents) {
					queue.add(key._key);
				}
				expression.mDependents.clear();
			}
		}
		for (final Entry<IDomain, Collection<WeakReference<IDependent>>> entry : postponed.asMap().entrySet()) {
			final IDomain domain = entry.getKey();
			final Collection<WeakReference<IDependent>> expressions = entry.getValue();
			domain.signal(expressions);
		}
	}

	private static class WeakKey {

		private final WeakReference<IDependent> _key;
		private final int _hash;

		public WeakKey(final IDependent key) {
			_key = new WeakReference<>(key);
			_hash = key.hashCode();
		}

		@Override
		public int hashCode() {
			return _hash;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof WeakKey) {
				final WeakKey otherKey = (WeakKey) obj;
				if (_hash != otherKey._hash) {
					return false;
				}
				return Objects.equal(otherKey._key.get(), _key.get());
			}
			return false;
		}
	}

	private class Property<T> implements IProperty<T> {

		public final View<T> mValue;

		public final Set<WeakKey> mDependents;

		public Property(final T pValue) {
			mValue = STM.newRef(pValue);
			mDependents = STM.newSet();
		}

		private T getImpl(final IDependent pDependent) {
			return STM.atomic(new Callable<T>() {
				@Override
				public T call() throws Exception {
					if (pDependent != null) {
						mDependents.add(new WeakKey(pDependent));
					}
					return mValue.get();
				}
			});
		}

		@Override
		public T get(final IDependent pDependent) {
			Preconditions.checkNotNull(pDependent);
			return getImpl(pDependent);
		}

		@Override
		public void set(final T pValue) {
			STM.atomic(new Runnable() {
				@Override
				public void run() {
					final T oldValue = mValue.get();
					if (Objects.equal(oldValue, pValue)) {
						return;
					}
					mValue.set(pValue);
					signal(Iterables.transform(mDependents, new Function<WeakKey, WeakReference<IDependent>>() {
						@Override
						public WeakReference<IDependent> apply(final WeakKey input) {
							return input._key;
						}
					}));
					mDependents.clear();
				}
			});
		}

		@Override
		public T output() {
			return getImpl(null);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper("Property").add("value", mValue).toString();
		}
	}

	private class Expression<T> implements IExpression<T> {

		public final Set<WeakKey> mDependents;

		public final View<Boolean> mIsUpToDate;

		public final View<T> mValue;

		public final Function<IDependent, T> mUpdate;

		public Expression(final Function<IDependent, T> f) {
			mDependents = STM.newSet();
			mUpdate = f;
			mIsUpToDate = STM.newRef(false);
			mValue = STM.newRef(null);
		}

		private T getImpl(final IDependent pDependent) {
			if (Objects.equal(mIsUpToDate.get(), Boolean.FALSE)) {
				mValue.set(mUpdate.apply(this));
				mIsUpToDate.set(Boolean.TRUE);;
			}
			if (pDependent != null) {
				mDependents.add(new WeakKey(pDependent));
			}
			return mValue.get();
		}

		@Override
		public T get(final IDependent pDependent) {
			Preconditions.checkNotNull(pDependent);
			return getImpl(pDependent);
		}

		@Override
		public T output() {
			return getImpl(null);
		}

		@Override
		public StmDomain getDomain() {
			return StmDomain.this;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(Expression.class).add("isUpToDate", mIsUpToDate).add("value", mValue).toString();
		}
	}
}
