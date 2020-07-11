package controller.menuitems;

import controller.Solver;
import controller.undoableedits.MarkStartOrGoalEdit;
import model.Graph;
import model.Node;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
* @version 1.0
 *
 * This control allows the user to mark a selected Node as the "start" Node for Solver operations.
 *
 * @see Graph
 * @see Solver
 * @see controller.MenuBar
 */
public class MarkStartMenuItem extends JMenuItem {

    private final Graph graph;

    /**
     * Constructs a MarkStartNodeMenuItem for the given graph.
     *
     * @param graph The GraphModel whose Nodes will be marked.
     */
    public MarkStartMenuItem(Graph graph) {
        super("Mark Start");
        setToolTipText("Mark the selected node as the start node for graph solving purposes.");

        this.graph = graph;

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());
        addActionListener(event -> {
            Node startNode = graph.getSelectedNodes().get(0);
            new MarkStartOrGoalEdit(graph, startNode, graph.getGoalNode());
        });

        setMnemonic(KeyEvent.VK_M);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enables or disables this button based on whether the graph has exactly 1 selected node.
     */
    private void setProperties() {
        setEnabled(graph.numSelectedNodes() == 1);
    }

}