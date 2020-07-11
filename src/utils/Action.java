package utils;

/**
* @version 2.0
 *
 * Interface for an action that can be performed on some object.
 *
 * @param <T> The class whose instances the action will operated on.
 */
public interface Action<T> {
    void doAction(T object);
}