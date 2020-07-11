package controller.menuitems;

import controller.MenuBar;
import controller.undoableedits.MarkStartOrGoalEdit;
import model.Graph;
import model.Node;
import controller.Solver;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
* @version 1.0
 *
 * This control allows the user to mark a selected Node as the "goal" Node for Solver operations.
 *
 * @see Graph
 * @see Solver
 * @see MenuBar
 */
public class MarkGoalNodeMenuItem extends JMenuItem {

    private final Graph graph;

    /**
     * Constructs a MarkGoalNodeMenuItem for the given graph.
     *
     * @param graph The GraphModel whose Nodes will be marked.
     */
    public MarkGoalNodeMenuItem(Graph graph) {
        super("Mark Goal Node");
        setToolTipText("Mark the selected node as the goal node for graph solving purposes.");

        this.graph = graph;

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());
        addActionListener(event -> {
            Node goalNode = graph.getSelectedNodes().get(0);
            new MarkStartOrGoalEdit(graph, graph.getStartNode(), goalNode);
        });

        setMnemonic(KeyEvent.VK_G);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enables or disables this button based on whether the graph has exactly 1 selected node.
     */
    private void setProperties() {
        setEnabled(graph.numSelectedNodes() == 1);
    }

}