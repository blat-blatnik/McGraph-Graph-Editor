package controller.menuitems;

import view.GraphPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
* @version 1.0
 *
 * This control allows the user to zoom out with the GraphPanel's view.
 *
 * @see GraphPanel
 */
public class ZoomOutMenuItem extends JMenuItem {

    /**
     * Constructs a ZoomOutMenuItem for the given panel.
     *
     * @param panel The GraphPanel whose view will be zoomed.
     */
    public ZoomOutMenuItem(GraphPanel panel) {
        super("Zoom Out");
        setToolTipText("Zoom out of the center of the view.");

        addActionListener(e -> panel.zoom(1.0 / 1.1));

        setMnemonic(KeyEvent.VK_O);
        setAccelerator(KeyStroke.getKeyStroke('-'));
    }

}