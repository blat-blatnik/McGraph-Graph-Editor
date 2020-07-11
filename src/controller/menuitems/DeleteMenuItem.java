package controller.menuitems;

import model.Graph;
import controller.undoableedits.RemoveNodesAndEdgesEdit;
import utils.KeyUtil;

import javax.swing.*;

/**
 * @author Boris
 * @version 1.0
 *
 * This control allows the user to delete selected Nodes and Edges from a graph.
 *
 * @see Graph
 * @see controller.MenuBar
 */
public class DeleteMenuItem extends JMenuItem {

    private final Graph graph;

    /**
     * Constructs a DeleteMenuItem for a given graph.
     *
     * @param graph The GraphModel whose selected Nodes and Edges will be deleted.
     */
    public DeleteMenuItem(Graph graph) {
        super("Delete");
        setToolTipText("Remove the selected nodes and edges.");

        this.graph = graph;
        setProperties();
        graph.addObserver((obj, msg) -> setProperties());
        addActionListener(e -> new RemoveNodesAndEdgesEdit(graph));

        setAccelerator(KeyStroke.getKeyStroke(KeyUtil.DELETE_KEY, 0));
    }

    /**
     * Enables or disables this button based on whether there are any selected Nodes or Edges to delete.
     */
    private void setProperties() {
        setEnabled(graph.numSelectedEdges() > 0 || graph.numSelectedNodes() > 0);
    }

}