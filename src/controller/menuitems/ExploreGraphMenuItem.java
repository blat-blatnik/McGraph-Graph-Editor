package controller.menuitems;

import model.Graph;
import controller.Solver;
import utils.KeyUtil;
import view.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
* @version 1.0
 *
 * This control allows the user to invoke the Solver to explore and mark the graph.
 *
 * @see Graph
 * @see Solver
 * @see controller.MenuBar
 */
public class ExploreGraphMenuItem extends JMenuItem {

    private final Graph graph;
    private final Solver solver;

    /**
     * Constructs a ExploreGraphMenuItem for the specified graph and panel.
     *
     * @param graph The GraphModel that the Solver will be invoked upon.
     * @param panel The GraphPanel whose cursor will be changed to the "WAIT_CURSOR" during the Solver operation.
     */
    public ExploreGraphMenuItem(Graph graph, GraphPanel panel) {
        super("Explore Graph");
        setToolTipText("Find the distance of all nodes in the graph from the currently marked start node.");

        this.graph = graph;
        this.solver = Solver.getInstance();

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());
        addActionListener(event -> {
            panel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            solver.exploreAndMarkWholeGraph(graph);
            panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        });

        setMnemonic(KeyEvent.VK_E);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enables or disables this buttons based on whether the graph has node labelled "start".
     */
    private void setProperties() {
        setEnabled(graph.getStartNode() != null);
    }

}