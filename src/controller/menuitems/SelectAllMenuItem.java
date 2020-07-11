package controller.menuitems;

import model.Edge;
import model.Graph;
import model.Node;
import controller.animation.NodeSizeAnimation;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
* @version 1.0
 *
 * This control allows the user to select all Nodes and Edges in the graph with a single action.
 *
 * @see Graph
 * @see controller.MenuBar
 */
public class SelectAllMenuItem extends JMenuItem {

    /**
     * Constructs a SelectAllMenuItem for the given graph.
     *
     * @param graph The GraphModel whose Nodes and Edges will be selected.
     */
    public SelectAllMenuItem(Graph graph) {
        super("Select All");
        setToolTipText("Select all nodes and edges.");

        addActionListener(e -> {

            List<Node> newlySelectedNodes = new ArrayList<>();
            for (Node node : graph.getNodes()) {
                if (!graph.isSelected(node)) {
                    newlySelectedNodes.add(node);
                    graph.select(node);
                }
            }

            for (Edge edge : graph.getEdges())
                graph.select(edge);

            new NodeSizeAnimation(newlySelectedNodes, 8, 8, 0.20).play();
        });

        setMnemonic(KeyEvent.VK_S);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyUtil.MENU_KEY_MASK));
    }

}