package be.functional.util.functional;

import com.google.common.base.Preconditions;

public class IntSuppliers {

   public static IntSupplier memoize(final IntSupplier src) {
      Preconditions.checkNotNull(src);
      return (src instanceof MemoizingIntSupplier)? src : new MemoizingIntSupplier(src);
   }

   private static class MemoizingIntSupplier implements IntSupplier {

      private IntSupplier mSrc;
      private volatile boolean mInitialized;
      private int mValue;

      public MemoizingIntSupplier(final IntSupplier src) {
         mSrc = src;
      }

      @Override
      public int get() {
         // A 2-field variant of Double Checked Locking.
         if (!mInitialized) {
           synchronized (this) {
             if (!mInitialized) {
               final int t = mSrc.get();
               mSrc = null;
               mValue = t;
               mInitialized = true;
               return t;
             }
           }
         }
         return mValue;
      }
   }
}
