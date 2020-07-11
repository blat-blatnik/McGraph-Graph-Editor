package controller.menuitems;

import model.Graph;
import controller.Solver;
import utils.KeyUtil;
import view.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @version 1.1
 *
 * This control allows the user to invoke the Solver to find the shortest path between the "start" and "goal" Nodes.
 *
 * @see Graph
 * @see Solver
 * @see controller.MenuBar
 */
public class FindShortestPathMenuItem extends JMenuItem {

    private final Graph graph;
    private final Solver solver;

    /**
     * Constructs a FindShortestPathMenuItem for the given graph and panel.
     *
     * @param graph The GraphModel that the Solver will be invoked on.
     * @param panel The GraphPanel whose cursor will be changed to "WAIT_CURSOR" during the Solver operation.
     */
    public FindShortestPathMenuItem(Graph graph, GraphPanel panel) {
        super("Find Shortest Path");
        setToolTipText("Find and label all nodes and edges along the shortest path between the marked start and goal nodes.");

        this.graph = graph;
        this.solver = Solver.getInstance();

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());
        addActionListener(event -> {
            panel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            solver.markShortestPath(graph);
            panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        });

        setMnemonic(KeyEvent.VK_F);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enables or disables this button based on whether the graph as a currently set "start" and "goal" node.
     */
    private void setProperties() {
        setEnabled(graph.getStartNode() != null && graph.getGoalNode() != null);
    }

}