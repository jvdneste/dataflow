package be.functional.util.functional.tuple;

import java.util.Comparator;

import javax.annotation.concurrent.Immutable;

import be.functional.util.functional.IntSupplier;
import be.functional.util.functional.IntSuppliers;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * Represents a tuple of 4 elements
 */
@Immutable
public class Quad<A,B,C,D> {

	public final A first;
	public final B second;
	public final C third;
	public final D fourth;

	/** use Quad.of instead */
	protected Quad(final A a, final B b, final C c, final D d) {
		this.first = a;
		this.second = b;
		this.third = c;
		this.fourth = d;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("first", first).add("second", second).add("third", third).add("fourth", fourth).toString();
	}

	private final IntSupplier mHashcode = IntSuppliers.memoize(new IntSupplier() {
		@Override
		public int get() {
			return Objects.hashCode(first, second, third, fourth);
		}
	});

	@Override
	public int hashCode() {
		assert mHashcode.get() == Objects.hashCode(first, second, third, fourth);
		return mHashcode.get();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Quad<?,?,?,?> other = (Quad<?,?,?,?>) obj;
		if (hashCode() != other.hashCode()) {
			return false;
		}
		return Objects.equal(first, other.first)
				&& Objects.equal(second, other.second)
				&& Objects.equal(third, other.third)
				&& Objects.equal(fourth, other.fourth);
	}

	public static <A,B,C,D> Quad<A,B,C,D> of(final A a, final B b, final C c, final D d) {
		return new Quad<A, B, C, D>(a, b, c, d);
	}

	@SuppressWarnings("unchecked")
	public static <A,B,C,D> Function<Quad<A,B,C,D>,A> extractFirst() {
		return ExtractFirst;
	}

	@SuppressWarnings("unchecked")
	public static <A,B,C,D> Function<Quad<A,B,C,D>,B> extractSecond() {
		return ExtractSecond;
	}

	@SuppressWarnings("unchecked")
	public static <A,B,C,D> Function<Quad<A,B,C,D>,C> extractThird() {
		return ExtractThird;
	}

	@SuppressWarnings("unchecked")
	public static <A,B,C,D> Function<Quad<A,B,C,D>,D> extractFourth() {
		return ExtractFourth;
	}

	@SuppressWarnings("rawtypes")
	private static final Function ExtractFirst = new Function() {
		@Override
		public Object apply(final Object from) {
			return ((Quad)from).first;
		}
	};

	@SuppressWarnings("rawtypes")
	private static final Function ExtractSecond = new Function() {
		@Override
		public Object apply(final Object from) {
			return ((Quad)from).second;
		}
	};

	@SuppressWarnings("rawtypes")
	private static final Function ExtractThird = new Function() {
		@Override
		public Object apply(final Object from) {
			return ((Quad)from).third;
		}
	};

	@SuppressWarnings("rawtypes")
	private static final Function ExtractFourth = new Function() {
		@Override
		public Object apply(final Object from) {
			return ((Quad)from).fourth;
		}
	};


	@SuppressWarnings("rawtypes")
	private static final Comparator RawComparator = new Comparator() {
		@Override
		public int compare(final Object o1, final Object o2) {
			final Quad t1 = (Quad) o1, t2 = (Quad) o2;
			return ComparisonChain.start()
					.compare((Comparable) t1.first, (Comparable) t2.first)
					.compare((Comparable) t1.second, (Comparable) t2.second)
					.compare((Comparable) t1.third, (Comparable) t2.third)
					.compare((Comparable) t1.fourth, (Comparable) t2.fourth)
					.result();
		}
	};

	/**
	 * A lexicographical Quad Comparator
	 */
	@SuppressWarnings("unchecked")
	public static <A extends Comparable<? super A>, B extends Comparable<? super B>, C extends Comparable<? super C>, D extends Comparable<? super D>>
	Comparator<Quad<? extends A, ? extends B, ? extends C, ? extends D>> comparator() {
		return RawComparator;
	}
}
