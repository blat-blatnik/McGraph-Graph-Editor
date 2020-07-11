package controller.menuitems;

import model.Edge;
import model.Graph;
import model.Node;
import controller.undoableedits.RemoveNodesAndEdgesEdit;
import controller.Clipboard;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Boris
 * @author Jana
 * @version 1.2
 *
 * This control allows the user to cut nodes or edges to the Clipboard so that they can be pasted later.
 *
 * @see Graph
 * @see Clipboard
 * @see controller.MenuBar
 */
public class CutMenuItem extends JMenuItem {

    private final Graph graph;
    private final Clipboard clipboard;

    /**
     * Constructs a CutMenuItem for the given graph.
     *
     * @param graph The GraphModel whose Nodes and Edges will be cut to the Clipboard.
     */
    public CutMenuItem(Graph graph) {
        super("Cut");
        setToolTipText("Cut the currently selected node, removing it from the graph and adding it to the clipboard.");
        this.graph = graph;
        this.clipboard = Clipboard.getInstance();

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());

        addActionListener(e -> {
            List<Node> selectedNodes = new ArrayList<>(graph.getSelectedNodes());
            List<Edge> selectedEdges = new ArrayList<>(graph.getSelectedEdges());
            clipboard.store(selectedNodes, selectedEdges);

            new RemoveNodesAndEdgesEdit(graph);
        });

        setMnemonic(KeyEvent.VK_T);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enables or disables this button based on whether there are any selected Nodes or Edges to copy.
     */
    private void setProperties() {
        setEnabled(graph.numSelectedNodes() > 0);
    }

}