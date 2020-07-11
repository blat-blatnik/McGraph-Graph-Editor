package controller.menuitems;

import view.GraphPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * @author Boris
 * @version 1.0
 *
 * This control allows the user to zoom in with the GraphPanel's view.
 *
 * @see GraphPanel
 * @see controller.MenuBar
 * @see controller.MenuBar
 */
public class ZoomInMenuItem extends JMenuItem {

    /**
     * Constructs a ZoomInMenuItem for the given panel.
     *
     * @param panel The GraphPanel whose view will be zoomed.
     */
    public ZoomInMenuItem(GraphPanel panel) {
        super("Zoom In");
        setToolTipText("Zoom in to the center of the view.");

        addActionListener(e -> panel.zoom(1.1));

        setMnemonic(KeyEvent.VK_I);
        setAccelerator(KeyStroke.getKeyStroke('+'));
    }

}