package utils;

/**
 * @author Boris
 * @version 1.0
 *
 * Represents a generic method of setting some property of some object that can be passed around as a parameter.
 *
 * @param <T> The class whose property to set.
 * @param <V> The class of the property to be set.
 */
public interface Setter<T, V> {
    /**
     * Sets the desired property of the given object using the given value.
     *
     * @param object The object whose property to set.
     * @param value The value from which the new property can be derived from.
     */
    void set(T object, V value);
}