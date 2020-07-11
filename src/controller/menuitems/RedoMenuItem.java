package controller.menuitems;

import model.Graph;
import utils.KeyUtil;

import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.awt.event.KeyEvent;

/**
 * @author Boris
 * @author Jana
 * @version 1.2
 *
 * This control allows the user to redo an edit previously undo-ed by the UndoMenuItem.
 *
 * @see Graph
 * @see UndoMenuItem
 * @see model.GraphUndoManager
 * @see controller.MenuBar
 */
public class RedoMenuItem extends JMenuItem {

    //NOTE(Boris): I thought about replacing this field with an UndoManager field from the graph. However since, the
    // undo manager of the graph isn't final it could change at any time - so we do have to fetch it from the graph
    // every time here..
    private final Graph graph;

    /**
     * Constructs a RedoMenuItem for the given GraphModel.
     *
     * @param graph The GraphModel whose GraphUndoManager will be used to redo.
     */
    public RedoMenuItem(Graph graph) {
        super("Redo");
        setToolTipText("Redo the last undone action.");

        this.graph = graph;

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());

        addActionListener(e -> graph.getUndoManager().redo());

        setMnemonic(KeyEvent.VK_R);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enables or disables this button based on whether there is some edit to redo.
     */
    private void setProperties() {
        UndoManager undoManager = graph.getUndoManager();
        setEnabled(undoManager.canRedo());
    }

}