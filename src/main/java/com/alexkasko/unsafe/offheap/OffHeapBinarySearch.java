package com.alexkasko.unsafe.offheap;

/**
 * Binary search implementation borrowed from {@code https://android.googlesource.com/platform/libcore/+/android-4.2.2_r1/luni/src/main/java/java/util/Arrays.java}
 * and adapted to {@link OffHeapAddressable}
 *
 * @author alexkasko
 * Date: 3/5/13
 * @see OffHeapLongSorter
 * @see OffHeapPayloadSorter
 */
public class OffHeapBinarySearch {
    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap collection.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     */
    public static long binarySearch(OffHeapAddressable collection, long value) {
        return binarySearch(collection, 0, collection.size(), value);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap collection,
     * in the range specified by fromIndex (inclusive) and toIndex (exclusive).
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection      the sorted collection to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value      the element to find.
     * @return the non-negative index of the element, or a negative index which
     *         is {@code -index - 1} where the element would be inserted.
     * @throws IllegalArgumentException {@code if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()}
     */
    public static long binarySearch(OffHeapAddressable collection, long startIndex, long endIndex, long value) {
        if (startIndex < 0 || startIndex > endIndex || endIndex > collection.size()) {
            throw new IllegalArgumentException("Illegal input, collection size: [" + collection.size() + "], " +
                    "startIndex: [" + startIndex + "], endIndex: [" + endIndex + "]");
        }
        long lo = startIndex;
        long hi = endIndex - 1;
        while (lo <= hi) {
            long mid = (lo + hi) >>> 1;
            long midVal = collection.get(mid);

            if (midVal < value) {
                lo = mid + 1;
            } else if (midVal > value) {
                hi = mid - 1;
            } else {
                return mid;  // value found
            }
        }
        return ~lo;  // value not present
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap collection.
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param value the element to find.
     * @return range of indices having given value or empty range
     */
    public static IndexRange binarySearchRange(OffHeapAddressable collection, long value) {
        return binarySearchRange(collection, 0, collection.size(), value);
    }

    /**
     * Performs a binary search for {@code value} in the ascending sorted off-heap collection.
     * Returns range of indices having given value or empty range.
     * Searching in an unsorted collection has an undefined result. It's also undefined which element
     * is found if there are multiple occurrences of the same element.
     *
     * @param collection the sorted array to search.
     * @param startIndex the inclusive start index.
     * @param endIndex   the exclusive end index.
     * @param value the element to find.
     * @return range of indices having given value or empty range
     */
    public static IndexRange binarySearchRange(OffHeapAddressable collection, long startIndex, long endIndex, long value) {
        long ind = binarySearch(collection, startIndex, endIndex, value);
        if(ind < 0) return new IndexRange(ind);
        long from = ind;
        while (from >= startIndex && value == collection.get(from)) from -= 1;
        from += 1;
        long to = ind;
        while (to < endIndex && value == collection.get(to)) to += 1;
        to -= 1;
        return new IndexRange(from, to);
    }

    /**
     * {@link OffHeapAddressable} index range representation.
     * {@link #isEmpty()} method should be checked before accessing indices.
     * Empty range will contain negative equal indices which values are
     * {@code -index - 1} where the element would be inserted.
     * Range with size {@code 1} will contain equal indices.
     */
    public static class IndexRange {
        private final boolean empty;
        private final long fromIndex;
        private final long toIndex;

        /**
         * Empty range constructor
         */
        private IndexRange(long value) {
            this.empty = true;
            this.fromIndex = value;
            this.toIndex = value;
        }

        /**
         * Non-empty range constructor
         *
         * @param from start index
         * @param to end index
         */
        private IndexRange(long from, long to) {
            this.empty = false;
            this.fromIndex = from;
            this.toIndex = to;
        }

        /**
         * Whether this range is empty
         *
         * @return whether this range is empty
         */
        public boolean isEmpty() {
            return empty;
        }

        /**
         * Whether this range is not empty
         *
         * @return whether this range is not empty
         */
        public boolean isNotEmpty() {
            return !empty;
        }

        /**
         * Start index or {@code -index - 1}
         *      where the element would be inserted for empty range
         *
         * @return start index value or {@code -index - 1}
         *      where the element would be inserted for empty range
         */
        public long getFromIndex() {
            return fromIndex;
        }

        /**
         * End index or {@code -index - 1}
         *      where the element would be inserted for empty range
         *
         * @return end index value, {@code -index - 1}
         *      where the element would be inserted for empty range
         */
        public long getToIndex() {
            return toIndex;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("IndexRange");
            sb.append("{empty=").append(empty);
            sb.append(", fromIndex=").append(fromIndex);
            sb.append(", toIndex=").append(toIndex);
            sb.append('}');
            return sb.toString();
        }
    }
}