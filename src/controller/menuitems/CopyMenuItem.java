package controller.menuitems;

import model.Graph;
import controller.Clipboard;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * @version 1.2
 *
 * This control allows the user to copy nodes or edges to the Clipboard so that they can be pasted later.
 *
 * @see Graph
 * @see Clipboard
 * @see controller.MenuBar
 */
public class CopyMenuItem extends JMenuItem {

    private final Graph graph;
    private final Clipboard clipboard;

    /**
     * Constructs a CopyMenuItem for the given graph.
     *
     * @param graph The GraphModel whose Nodes and Edges will be copied to the Clipboard.
     */
    public CopyMenuItem(Graph graph) {
        super("Copy");
        setToolTipText("Copy the currently selected nodes to the clipboard.");

        this.graph = graph;
        this.clipboard = Clipboard.getInstance();

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());
        addActionListener(e -> clipboard.store(graph.getSelectedNodes(), graph.getSelectedEdges()));

        setMnemonic(KeyEvent.VK_C);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enables or disables this button based on whether there are any selected Nodes or Edges to copy.
     */
    private void setProperties() {
        setEnabled(graph.numSelectedNodes() > 0);
    }

}