package utils;

import javax.swing.*;
import javax.swing.Action;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
* @version 1.1
 *
 * This class contains some utility methods and fields related to keybindings.
 *
 * It also holds some platform specific values that we want to account for. Even though java is
 * supposed to be completely cross platform, well nothing can be cross platform without extra work.
 *
 * @see Toolkit
 * @see KeyEvent
 */
public final class KeyUtil {

    /**
     * This value is true only if the application is running on a Mac.
     */
    public static final boolean IS_MAC_OS = System.getProperty("os.name").toLowerCase().startsWith("mac ");

    /**
     * This is a bit-field mask for the platform specific modifier key that is supposed to be used for shortcuts for
     * the particular platform. On Windows/Linux it maps to the 'control' key, on Mac it maps to 'command' key.
     */
    public static final int MENU_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

    /**
     * This is the key that is commonly used on the platform to 'delete' things. Even though on Mac, the 'backspace' key
     * is ACTUALLY called 'delete', VK_DELETE seems to map to no key at all on a mac so we need to remap ourselves.
     */
    public static final int DELETE_KEY = IS_MAC_OS ? KeyEvent.VK_BACK_SPACE : KeyEvent.VK_DELETE;

    /**
     * @param event The MouseEvent to check.
     * @return Whether the menu key modifier was pressed down during the given event (control for windows/linux, command for mac).
     * @see MouseEvent
     * @see KeyEvent
     */
    public static boolean isMenuModifierDown(MouseEvent event) {
        return (event.getModifiers() & MENU_KEY_MASK) != 0;
    }

    /**
     * Maps the given Action to the InputMap and ActionMap of the given component. The Action will be triggered when
     * the user presses the keyCode with the given modifier keys held down.
     *
     * @param component The component whose InputMap and ActionMap to add the Action to.
     * @param keyCode The key code that will trigger the Action.
     * @param modifiers The modifiers that must be held down to trigger the Action.
     * @param action The Action to perform when the key combination is pressed.
     */
    public static void addPressAction(JComponent component, int keyCode, int modifiers, Action action) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();
        String name = "press " + keyCode + modifiers;
        inputMap.put(KeyStroke.getKeyStroke(keyCode, modifiers, false), name);
        actionMap.put(name, action);
    }

    /**
     * This class contains only static fields and methods and should never be instantiated.
     */
    private KeyUtil() {}

}