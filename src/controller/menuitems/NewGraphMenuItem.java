package controller.menuitems;

import controller.undoableedits.NewGraphEdit;
import model.Graph;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
* @version 1.0
 *
 * This control allows the user to completely clear the current graph of all Nodes and Edges.
 *
 * @see Graph
 * @see controller.MenuBar
 */
public class NewGraphMenuItem extends JMenuItem {

    /**
     * Constructs a NewGraphMenuItem for the specified graph.
     *
     * @param graph The GraphModel whose Nodes and Edges will be cleared.
     */
    public NewGraphMenuItem(Graph graph) {
        super("New Graph");
        setToolTipText("Clear the current graph and start a new one.");

        addActionListener(e -> new NewGraphEdit(graph));

        setMnemonic(KeyEvent.VK_N);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyUtil.MENU_KEY_MASK));
    }

}