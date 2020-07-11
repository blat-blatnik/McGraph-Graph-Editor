package controller.menuitems;

import model.Graph;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
* @version 1.0
 *
 * This control allows the user to deselect all currently selected Nodes and Edges of a graph.
 *
 * @see Graph
 * @see controller.MenuBar
 */
public class DeselectMenuItem extends JMenuItem {

    private final Graph graph;

    /**
     * Constructs a DeselectMenuItem for the given GraphModel.
     *
     * @param graph The GraphModel whose selected Nodes and Edges will be deselected.
     */
    public DeselectMenuItem(Graph graph) {
        super("Deselect");
        setToolTipText("Deselect the currently selected nodes or edges.");

        this.graph = graph;

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());
        addActionListener(e -> {
            if (graph.isAddingEdges())
                graph.stopAddingEdges();
            graph.clearSelectedNodes();
            graph.clearSelectedEdges();
        });

        setMnemonic(KeyEvent.VK_D);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enables or disables this button based on whether any Nodes or Edges are selected.
     */
    private void setProperties() {
        setEnabled(graph.numSelectedNodes() > 0 || graph.numSelectedEdges() > 0 || graph.isAddingEdges());
    }

}