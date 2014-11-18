package be.functional.util.functional.tuple;

import com.google.common.base.MoreObjects;

public class IntPair {

	public final int first;
	public final int second;

	private final int hashCode;

	/** use IntPair.of instead */
	protected IntPair(final int a, final int b) {
		first = a;
		second = b;

		hashCode = (31 * (31 + a)) + b;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("first", first).add("second", second).toString();
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final IntPair other = (IntPair) obj;
		return (first == other.first) && (second == other.second);
	}

	public static IntPair of(final int a, final int b) {
		return new IntPair(a, b);
	}
}
