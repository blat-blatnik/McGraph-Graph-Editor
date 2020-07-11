package controller;

import controller.menuitems.*;
import controller.undoableedits.AddNodesAndEdgesEdit;
import controller.undoableedits.UnfinishedEdgeEdit;
import model.Graph;
import model.Node;
import utils.KeyUtil;
import view.GraphPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
* @version 1.2
 *
 * This PopupMenu appears when the user right-clicks on the GraphPanel. It provides some shortcuts to commonly used
 * controls.
 *
 * @see MarkStartMenuItem
 * @see MarkGoalMenuItem
 * @see CutMenuItem
 * @see CopyMenuItem
 * @see PasteMenuItem
 * @see ExploreGraphMenuItem
 * @see FindShortestPathMenuItem
 * @see ColorGraphMenuItem
 * @see CenterViewMenuItem
 */
public class PopupMenu extends JPopupMenu {

    /**
     * Construct a new PopupMenu for the given GraphModel and GraphPanel.
     *
     * @param graph The GraphModel that this PopupMenu will control.
     * @param panel The GraphPanel that this PopupMenu will control.
     */
    public PopupMenu(Graph graph, GraphPanel panel) {
        super("Edit");

        JMenuItem addNode = new JMenuItem("Add Node");
        JMenuItem addEdge = new JMenuItem("Add Edge");

        addNode.setToolTipText("Add a new node to the graph.");
        addEdge.setToolTipText("Add a new edge to the graph.");

        addNode.addActionListener(e -> {
            List<Node> newNodes = new ArrayList<>();
            Point2D location = panel.projectToGraphSpace(panel.getMousePosition());
            Node newNode = new Node("New Node", location.getX(), location.getY());
            newNodes.add(newNode);
            new AddNodesAndEdgesEdit(graph, newNodes, null);
        });

        addEdge.addActionListener(e -> {
            Point2D location = panel.projectToGraphSpace(panel.getMousePosition());
            new UnfinishedEdgeEdit(graph, graph.getSelectedNodes(), location.getX(), location.getY());
        });

        graph.addObserver((obj, msg) -> addEdge.setEnabled(graph.numSelectedNodes() > 0 && !graph.isAddingEdges()));

        addNode.setMnemonic(KeyEvent.VK_N);
        addEdge.setMnemonic(KeyEvent.VK_E);
        addNode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyUtil.MENU_KEY_MASK | KeyEvent.SHIFT_DOWN_MASK));
        addEdge.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyUtil.MENU_KEY_MASK | KeyEvent.SHIFT_DOWN_MASK));

        add(new MarkStartMenuItem(graph));
        add(new MarkGoalMenuItem(graph));
        addSeparator();
        add(addNode);
        add(addEdge);
        addSeparator();
        add(new CutMenuItem(graph));
        add(new CopyMenuItem(graph));
        add(new PasteMenuItem(graph));
        addSeparator();
        add(new ExploreGraphMenuItem(graph, panel));
        add(new FindShortestPathMenuItem(graph, panel));
        add(new ColorGraphMenuItem(graph, panel));
        addSeparator();
        add(new CenterViewMenuItem(panel));
    }

}