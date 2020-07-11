package controller.menuitems;

import model.Graph;
import controller.Solver;
import utils.KeyUtil;
import view.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Boris
 * @version 1.0
 *
 * This control invokes the graph-coloring functionality of the Solver.
 *
 * @see Graph
 * @see Solver
 * @see GraphPanel
 * @see MenuBar
 */
public class ColorGraphMenuItem extends JMenuItem {

    private final Graph graph;
    private final Solver solver;

    /**
     * Constructs a ColorGraphMenuItem for the given Graph and the given GraphPanel.
     *
     * @param graph The GraphModel whose nodes will be colored by the Solver.
     * @param panel The GraphPanel whose cursor will be changed to "WAIT_CURSOR" during the solver operation.
     */
    public ColorGraphMenuItem(Graph graph, GraphPanel panel) {
        super("Color Graph");
        setToolTipText("Find a graph node coloring using a greedy algorithm.");

        this.graph = graph;
        this.solver = Solver.getInstance();


        setProperties();
        graph.addObserver((obj, msg) -> setProperties());
        addActionListener(event -> {
            panel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            solver.colorGraph(graph);
            panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        });

        setMnemonic(KeyEvent.VK_C);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enable or disable this menu item based on whether the graph has any nodes to color.
     */
    private void setProperties() {
        setEnabled(graph.getNodes().size() > 0);
    }

}