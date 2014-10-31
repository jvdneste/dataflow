package be.functional.util.functional.tuple;

import java.util.Comparator;

import javax.annotation.concurrent.Immutable;

import be.functional.util.functional.IntSupplier;
import be.functional.util.functional.IntSuppliers;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

/**
 * Represents a tuple of 3 elements
 */
@Immutable
public class Triple<A,B,C> {

	public final A first;
	public final B second;
	public final C third;

	/** use Triple.of instead */
	protected Triple(final A a, final B b, final C c) {
		this.first = a;
		this.second = b;
		this.third = c;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("first", first).add("second", second).add("third", third).toString();
	}

   private final IntSupplier mHashcode = IntSuppliers.memoize(new IntSupplier() {
      @Override
      public int get() {
         return Objects.hashCode(first, second, third);
      }
   });

   @Override
   public int hashCode() {
      assert mHashcode.get() == Objects.hashCode(first, second, third);
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
		final Triple<?,?,?> other = (Triple<?,?,?>) obj;
		if (hashCode() != other.hashCode()) {
			return false;
		}
		return Objects.equal(first, other.first)
			&& Objects.equal(second, other.second)
			&& Objects.equal(third, other.third);
	}

	public static <A,B,C> Triple<A,B,C> of(final A a, final B b, final C c) {
		return new Triple<A, B, C>(a, b, c);
	}

	@SuppressWarnings("unchecked")
	public static <A,B,C> Function<Triple<A,B,C>,A> extractFirst() {
		return ExtractFirst;
	}

	@SuppressWarnings("unchecked")
	public static <A,B,C> Function<Triple<A,B,C>,B> extractSecond() {
		return ExtractSecond;
	}

	@SuppressWarnings("unchecked")
	public static <A,B,C> Function<Triple<A,B,C>,C> extractThird() {
		return ExtractThird;
	}

	@SuppressWarnings("rawtypes")
	private static final Function ExtractFirst = new Function() {
		@Override
		public Object apply(final Object from) {
			return ((Triple)from).first;
		}
	};

	@SuppressWarnings("rawtypes")
	private static final Function ExtractSecond = new Function() {
		@Override
		public Object apply(final Object from) {
			return ((Triple)from).second;
		}
	};

	@SuppressWarnings("rawtypes")
	private static final Function ExtractThird = new Function() {
		@Override
		public Object apply(final Object from) {
			return ((Triple)from).third;
		}
	};

   @SuppressWarnings("rawtypes")
   private static final Comparator RawComparator = new Comparator() {
      @Override
      public int compare(final Object o1, final Object o2) {
         final Triple t1 = (Triple) o1, t2 = (Triple) o2;
         return ComparisonChain.start()
               .compare((Comparable) t1.first, (Comparable) t2.first)
               .compare((Comparable) t1.second, (Comparable) t2.second)
               .compare((Comparable) t1.third, (Comparable) t2.third)
               .result();
      }
   };

   /**
    * A lexicographical Triple Comparator
    */
   @SuppressWarnings("unchecked")
   public static <A extends Comparable<? super A>, B extends Comparable<? super B>, C extends Comparable<? super C>>
      Comparator<Triple<? extends A, ? extends B, ? extends C>> comparator() {
      return RawComparator;
   }
}
