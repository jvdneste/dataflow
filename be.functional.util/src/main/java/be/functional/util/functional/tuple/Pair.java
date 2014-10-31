package be.functional.util.functional.tuple;

import java.util.Comparator;

import javax.annotation.concurrent.Immutable;

import be.functional.util.functional.IntSupplier;
import be.functional.util.functional.IntSuppliers;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

/**
 * Represents a tuple of 2 elements
 */
@Immutable
public class Pair<A, B> {

  public final A first;
  public final B second;

  /** use Pair.of instead */
  protected Pair(final A a, final B b) {
    first = a;
    second = b;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("first", first).add("second", second).toString();
  }

  private final IntSupplier mHashcode = IntSuppliers.memoize(new IntSupplier() {
    @Override
    public int get() {
      return Objects.hashCode(first, second);
    }
  });

  @Override
  public int hashCode() {
    assert mHashcode.get() == Objects.hashCode(first, second);
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
    final Pair<?,?> other = (Pair<?,?>) obj;
    if (hashCode() != other.hashCode()) {
      return false;
    }
    return Objects.equal(first, other.first) && Objects.equal(second, other.second);
  }

  public static <A,B> Pair<A, B> of(final A a, final B b) {
    return new Pair<A,B>(a, b);
  }

  @SuppressWarnings("rawtypes")
  private static final Function ExtractFirst = new Function() {
    @Override
    public Object apply(final Object from) {
      Preconditions.checkNotNull(from);
      return ((Pair)from).first;
    }
  };

  @SuppressWarnings("unchecked")
  public static <A> Function<Pair<A,?>,A> extractFirst() {
    return ExtractFirst;
  }

  @SuppressWarnings("rawtypes")
  private static final Function ExtractSecond = new Function() {
    @Override
    public Object apply(final Object from) {
      Preconditions.checkNotNull(from);
      return ((Pair)from).second;
    }
  };

  @SuppressWarnings("unchecked")
  public static <B> Function<Pair<?,B>,B> extractSecond() {
    return ExtractSecond;
  }

  @SuppressWarnings("rawtypes")
  private static final Comparator RawComparator = new Comparator() {
    @Override
    public int compare(final Object o1, final Object o2) {
      final Pair p1 = (Pair) o1, p2 = (Pair) o2;
      return ComparisonChain.start().compare((Comparable) p1.first, (Comparable) p2.first).compare((Comparable) p1.second, (Comparable) p2.second).result();
    }
  };

  /**
   * A lexicographical Pair Comparator
   */
  @SuppressWarnings("unchecked")
  public static <A extends Comparable<? super A>, B extends Comparable<? super B>> Comparator<Pair<? extends A, ? extends B>> comparator() {
    return RawComparator;
  }
}
