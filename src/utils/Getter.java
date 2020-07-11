package utils;

/**
 * @author Boris
 * @version 1.0
 *
 * Represents a generic method of getting some property from some object that can be passed around as a parameter.
 *
 * @param <T> The class to get the desired property from.
 * @param <V> The class of the desired property.
 */
public interface Getter<T, V> {
    /**
     * @param object The object from which to get the desired property from.
     * @return The desired property.
     */
    V get(T object);
}