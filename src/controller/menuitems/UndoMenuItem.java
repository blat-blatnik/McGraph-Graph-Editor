package controller.menuitems;

import model.Graph;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * @author Boris
 * @author Jana
 * @version 1.2
 *
 * This control allows the user to undo an edit previously performed on the graph.
 *
 * @see Graph
 * @see RedoMenuItem
 * @see model.GraphUndoManager
 * @see controller.MenuBar
 */
public class UndoMenuItem extends JMenuItem {

    //NOTE(Boris): I thought about replacing this field with an UndoManager field from the graph. However since, the
    // undo manager of the graph isn't final it could change at any time - so we do have to fetch it from the graph
    // every time here..
    private final Graph graph;

    /**
     * Constructs a UndoMenuItem for the given graph.
     *
     * @param graph The GraphModel whose GraphUndoManager will be used to undo.
     */
    public UndoMenuItem(Graph graph) {
        super("Undo");
        setToolTipText("Undo the last action.");

        this.graph = graph;

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());

        addActionListener(e -> graph.getUndoManager().undo());

        setMnemonic(KeyEvent.VK_U);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enables or disables this button based on whether there is some edit to undo.
     */
    private void setProperties() {
        setEnabled(graph.getUndoManager().canUndo());
    }

}