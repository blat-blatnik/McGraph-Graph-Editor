package utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Boris
 * @version 1.0
 *
 * Represents an ArrayList with a capped maximum item capacity. This is particularly useful for the Animation list as
 * having too many Animations running at once cripples performance.
 *
 * @param <T> The type stored in this BoundedList.
 * @see java.util.List
 * @see ArrayList
 * @see controller.animation.Animation
 */
public class BoundedList<T> extends ArrayList<T> {

    private final int maxCapacity;

    /**
     * Constructs a new BoundedList with a specified maximum capacity.
     *
     * @param maxCapacity The maximum number of items this BoundedList can hold.
     */
    public BoundedList(int maxCapacity) {
        super(maxCapacity);
        this.maxCapacity = maxCapacity;
    }

    /**
     * Adds a given item to the BoundedList, if the List is not at full capacity. If the List is at full capacity then
     * the item is not added.
     *
     * @param item The item to add to the List.
     * @return True if the item was added. False, if the maximum capacity has been reached and the item was not added.
     */
    public boolean add(T item) {
        if (size() < maxCapacity) {
            super.add(item);
            return true;
        } else
            return false;
    }

    /**
     * Adds all items from the given Collection into this BoundedList, provided that the List has enough spare capacity.
     * If the List does not have the capacity to store all of the items - items are added until the List is at maximum
     * capacity.
     *
     * @param items The items to add to the List.
     * @return Whether any items were added to the List.
     */
    public boolean addAll(Collection<? extends T> items) {
        for (T item : items)
            if (!add(item))
                return false;
        return true;
    }
}