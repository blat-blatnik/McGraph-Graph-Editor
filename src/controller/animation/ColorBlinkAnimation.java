package controller.animation;

import utils.Getter;
import utils.MathUtil;
import utils.Setter;

import java.awt.*;
import java.util.List;

/**
* @version 2.0
 *
 * Represents an Animation that blinks the Color of a list of objects between a Color, and it's lighter or darker
 * variant. This is a generic class that can be used to blink the Color's of any class T, in a fully generic manner.
 * It is used for blinking the Colors of Nodes and Edges.
 *
 * NOTE that this Animation runs indefinitely. Once you call play(), you need to manually call stop() when you don't
 * need the Animation to play anymore. If you don't do this the Animation will keep playing forever.
 *
 * @param <T> The class whose Color to blink.
 *
 * @see Animation
 */
public class ColorBlinkAnimation<T> extends Animation {

    /**
     * Represents a method used to get a List of objects whose color to blink.
     *
     * @param <T> The class whose Color to blink.
     */
    public interface ObjectGetter<T> {
        /**
         * @return A List of Objects whose Color to blink.
         */
        List<T> getObjects();
    }

    /**
     * Constructs a ColorBlinkingAnimation from the given ObjectGetter and a ColorGetter and ColorSetter for the
     * Colors to blink. Every Animation tick, this Animation will loop over all objects retrieved from the ObjectGetter,
     * retrieve their color using the ColorGetter, and then set their new Color using the ColorSetter.
     *
     * @param blinkedObjectGetter The ObjectGetter used to retrieve a List of objects whose color to blink.
     * @param colorGetter The Getter used to retrieve Colors that will be blinked from a Node.
     * @param colorSetter The Setter used to apply the blinked Colors to the Node.
     * @see Animation
     */
    public ColorBlinkAnimation(ObjectGetter<T> blinkedObjectGetter, Getter<T, Color> colorGetter, Setter<T, Color> colorSetter) {
        super(animation -> {
            double t = animation.getCurrentTime();
            for (T object : blinkedObjectGetter.getObjects()) {
                Color from = colorGetter.get(object);
                Color to = MathUtil.darkerOrLighterColor(from, 0.4f);
                Color interpolatedColor = MathUtil.lerp(from, to, Math.abs(Math.sin(4 * t)));
                colorSetter.set(object, interpolatedColor);
            }
        });
    }

}