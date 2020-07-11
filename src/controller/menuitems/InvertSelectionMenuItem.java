package controller.menuitems;

import model.Edge;
import model.Graph;
import model.Node;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Boris
 * @version 1.0
 *
 * This control allows the user to invert their selected Nodes and Edges. So if nodeA and nodeB are selected, but nodeC
 * is not, then nodeC will become selected and nodeA and nodeB will be deselected.
 *
 * @see Graph
 * @see controller.MenuBar
 */
public class InvertSelectionMenuItem extends JMenuItem {

    private final Graph graph;

    /**
     * Constructs a InvertSelectionMenuItem for the given GraphModel.
     *
     * @param graph The GraphModel whose selected Nodes and Edges will be inverted.
     */
    public InvertSelectionMenuItem(Graph graph) {
        super("Invert Selection");
        setToolTipText("Inverts the current selection of nodes and edges - selected items will be deselected, and deselected items will be selected.");

        this.graph = graph;

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());
        addActionListener(e -> {
            Set<Node> selectedNodes = new HashSet<>(graph.getSelectedNodes());
            Set<Edge> selectedEdges = new HashSet<>(graph.getSelectedEdges());
            for (Node node : graph.getNodes()) {
                if (selectedNodes.contains(node))
                    graph.deselect(node);
                else
                    graph.select(node);
            }
            for (Edge edge : graph.getEdges()) {
                if (selectedEdges.contains(edge))
                    graph.deselect(edge);
                else
                    graph.select(edge);
            }
        });

        setMnemonic(KeyEvent.VK_I);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyUtil.MENU_KEY_MASK | KeyEvent.SHIFT_DOWN_MASK));
    }

    /**
     * Enables or disables this button based on whether any Nodes or Edges are are selected in the graph.
     */
    private void setProperties() {
        setEnabled(graph.numSelectedNodes() > 0 || graph.numSelectedEdges() > 0);
    }

}