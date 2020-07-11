package controller.menuitems;

import utils.KeyUtil;
import view.GraphPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
* @version 1.0
 *
 * This control allows the user to center the panel's view on the current graph.
 *
 * @see GraphPanel
 * @see controller.MenuBar
 */
public class CenterViewMenuItem extends JMenuItem {

    /**
     * Constructs a CenterViewMenuItem for a given panel.
     *
     * @param panel The GraphPanel whose view will be centered.
     */
    public CenterViewMenuItem(GraphPanel panel) {
        super("Center");
        setToolTipText("Center the view on the graph.");

        addActionListener(e -> panel.centerViewOnGraph());

        setMnemonic(KeyEvent.VK_C);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyUtil.MENU_KEY_MASK | KeyEvent.SHIFT_DOWN_MASK));
    }

}