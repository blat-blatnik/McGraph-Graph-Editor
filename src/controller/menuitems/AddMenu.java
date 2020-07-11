package controller.menuitems;

import controller.undoableedits.AddNodesAndEdgesEdit;
import controller.undoableedits.UnfinishedEdgeEdit;
import model.Graph;
import model.Node;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
* @version 3.0
 *
 * This JMenu has 2 options that allow the user to add either a Node or an Edge to the GraphModel.
 *
 * @see Node
 * @see model.Edge
 * @see controller.MenuBar
 * @see Graph
 */
public class AddMenu extends JMenu {

    /**
     * Constructs a new AddMenu for the specified GraphModel.
     *
     * @param graph The GraphModel to which new Nodes and Edges will be added.
     */
    public AddMenu(Graph graph) {
        super("Add");

        JMenuItem addNode = new JMenuItem("Node");
        JMenuItem addEdge = new JMenuItem("Edge");

        addNode.setToolTipText("Add a new node to the graph.");
        addEdge.setToolTipText("Add a new edge to the graph.");

        graph.addObserver((obj, msg) -> addEdge.setEnabled(graph.numSelectedNodes() > 0 && !graph.isAddingEdges()));

        addNode.addActionListener(e -> {
            Rectangle2D graphBounds = graph.getBounds();
            Node newNode = new Node(graphBounds.getCenterX(), graphBounds.getCenterY());
            new AddNodesAndEdgesEdit(graph, Arrays.asList(newNode), null);
        });

        addEdge.addActionListener(e -> {
            Rectangle2D bounds = graph.getBounds();
            new UnfinishedEdgeEdit(graph, graph.getSelectedNodes(), bounds.getCenterX(), bounds.getCenterY());
        });

        setMnemonic(KeyEvent.VK_A);
        addNode.setMnemonic(KeyEvent.VK_N);
        addEdge.setMnemonic(KeyEvent.VK_E);
        addNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                KeyUtil.MENU_KEY_MASK | KeyEvent.SHIFT_DOWN_MASK));
        addEdge.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                KeyUtil.MENU_KEY_MASK | KeyEvent.SHIFT_DOWN_MASK));

        add(addNode);
        add(addEdge);
    }

}