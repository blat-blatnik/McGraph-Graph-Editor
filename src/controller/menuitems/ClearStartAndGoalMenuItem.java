package controller.menuitems;

import controller.MenuBar;
import controller.undoableedits.MarkStartOrGoalEdit;
import model.Graph;
import controller.Solver;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * @author Boris
 * @version 1.0
 *
 * This control allows the user to clear the nodes that were marked as either the "start" or "goal" nodes for
 * Solver operations.
 *
 * @see Graph
 * @see Solver
 * @see MenuBar
 */
public class ClearStartAndGoalMenuItem extends JMenuItem {

    private final Graph graph;

    /**
     * Constructs a ClearStartAndGoalMenuItem for the given graph.
     *
     * @param graph The GraphModel whose start and goal nodes will be cleared.
     */
    public ClearStartAndGoalMenuItem(Graph graph) {
        super("Clear Start & Goal");
        setToolTipText("Clear the 'start' and 'goal' label from the start and goal nodes.");
        this.graph = graph;

        graph.addObserver((obj, msg) -> setProperties());
        addActionListener(event -> new MarkStartOrGoalEdit(graph, null, null));

        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyUtil.MENU_KEY_MASK | KeyEvent.SHIFT_DOWN_MASK));
    }

    /**
     * Enable or disable this menu item based on whether the graph has a "start" or "goal" Node.
     */
    private void setProperties() {
        setEnabled(graph.getStartNode() != null || graph.getGoalNode() != null);
    }

}