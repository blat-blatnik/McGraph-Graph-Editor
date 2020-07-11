package utils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * @author Boris
 * @version 2.0
 *
 * This utility class loads all available system fonts and stores them in a convenient central place. It also contains
 * some common useful methods for manipulating text.
 *
 * @see Font
 * @see GraphicsEnvironment
 */
public final class TextUtil {

    private static final DecimalFormat FORMATTER = new DecimalFormat();

    /**
     * This array holds the font names of all fonts installed on the user's machine.
     */
    public static final String[] ALL_FONT_NAMES =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

    //NOTE(Boris): Sort so we can binary search through them.
    static { Arrays.sort(ALL_FONT_NAMES); }

    /**
     * @param fontName The font name to look up.
     * @return Whether the user has a font installed on their machine whose name matches the given name.
     */
    public static boolean fontExists(String fontName) {
        return Arrays.binarySearch(ALL_FONT_NAMES, fontName) >= 0;
    }

    /**
     * Formats a number into a nice looking string fit for displaying to the user. Since java's printf sucks this is
     * the only way to get 1.0 formatted simply as "1" instead of "1.000000". As an added bonus, it also formats
     * INFINITY as the infinity symbol.
     *
     * @param number The number to format.
     * @return The String containing the formatted number.
     * @see DecimalFormat
     */
    public static String format(double number) {
        return FORMATTER.format(number);
    }

    /**
     * @param g A Graphics2D object from which to get FontMetrics for the String.
     * @param string The String whose bounding Rectangle to get.
     * @return A bounding Rectangle that can completely enclose the given String if it was to be painted with g at (0, 0).
     */
    public static Rectangle2D getStringBounds(Graphics2D g, String string) {
        FontMetrics metrics = g.getFontMetrics();
        return metrics.getStringBounds(string, g);
    }

    /**
     * This class contains only static fields and methods and should never be instantiated.
     */
    private TextUtil() {}

}