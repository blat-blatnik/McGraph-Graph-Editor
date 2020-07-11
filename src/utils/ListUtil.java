package utils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
* @version 1.2
 *
 * This utility class has commonly used functions for querying and manipulating lists and arrays.
 *
 * @see List
 */
public final class ListUtil {

    /**
     * @param list The list whose elements to test.
     * @param <T> The type stored in the list.
     * @param predicate The function applied to all elements of the list.
     * @return Whether the given predicate applies to all elements of the given list.
     * @see List
     */
    public static <T> boolean all(List<T> list, Predicate<T> predicate) {
        for (T item : list) {
            if (!predicate.test(item))
                return false;
        }
        return true;
    }

    /**
     * @param strings The array of strings in which to find the longest string.
     * @return The longest (by number of characters) string in the given array, or null if the array is empty.
     * @see String
     */
    public static String getLongest(String[] strings) {
        if (strings.length == 0)
            return null;

        int longestIndex = 0;
        for (int i = 0; i < strings.length; ++i) {
            if (strings[i].length() > strings[longestIndex].length())
                longestIndex = i;
        }

        return strings[longestIndex];
    }

    /**
     * @param items The array of items in which to find the index of the given item.
     * @param <T> The type of the items stored in the array, and of the item to look for.
     * @param item The item whose index to find.
     * @return The index of the given item in the given array, or -1 if the item is not in the given array.
     */
    public static <T> int indexOf(T[] items, T item) {
        return Arrays.asList(items).indexOf(item);
    }

    /**
     * This class contains only static methods and should never be instantiated.
     */
    private ListUtil() {}
}