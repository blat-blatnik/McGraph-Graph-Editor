package controller.toolbaritems;

import model.Graph;
import model.Node;
import controller.undoableedits.NodeEdit;
import utils.KeyUtil;
import utils.MathUtil;
import utils.TextUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Boris
 * @author Jana
 * @version 2.0
 *
 * The user can use this control to change the text of the selected Nodes in a graph.
 *
 * @see Node
 * @see controller.NodeToolBar
 */
public class NodeTextField extends JTextField{

    private final Graph graph;
    private List<Node> selectedNodes;
    private List<Node> oldNodes;
    private boolean shouldChangeNodes;

    /**
     * Constructs a NodeTextField for the given graph.
     *
     * @param graph The GraphModel whose Nodes will have their text changed.
     */
    public NodeTextField(Graph graph) {
        super(16);
        setToolTipText("Enter node text");

        this.graph = graph;
        this.selectedNodes = new ArrayList<>();
        this.oldNodes = new ArrayList<>();
        this.shouldChangeNodes = true;

        setProperties();
        graph.addObserver((obj, msg) -> setProperties());

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSelectedNodeText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSelectedNodeText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSelectedNodeText();
            }
        });

        addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                selectedNodes = new ArrayList<>(graph.getSelectedNodes());
                oldNodes = new ArrayList<>(selectedNodes.size());
                for (Node node: selectedNodes)
                    oldNodes.add(new Node(node));
            }

            @Override
            public void focusLost(FocusEvent e) {

                // NOTE: Even though this doesn't look like it's doing anything to the nodes (and it doesn't), it
                // will still add the edit to the undo manager so that we can undo the text change, since oldNodes has
                // the old version of the node text..
                new NodeEdit(graph, selectedNodes, oldNodes, node -> {});

                selectedNodes = new ArrayList<>();
                oldNodes = new ArrayList<>();

            }
        });

        KeyUtil.addPressAction(this, KeyEvent.VK_F2, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestFocus();
                selectAll();
            }
        });
    }

    /**
     * Updates the text of all selected Nodes in the graph to match the text in the text field. If a Node is too small
     * to contain all of the text - then it is made larger so that the text will fit.
     */
    private void updateSelectedNodeText() {
        if (!shouldChangeNodes)
            return;

        String newText = getText();
        Graphics2D g = (Graphics2D)getGraphics();

        for (Node node : graph.getSelectedNodes()) {
            node.setActualName(newText);

            //NOTE(Boris): If the node is too small to contain the text, make it larger.
            Rectangle2D textBounds = TextUtil.getStringBounds(g, newText);
            Rectangle2D nodeBounds = node.getActualBounds();
            double x = nodeBounds.getCenterX();
            double y = nodeBounds.getCenterY();
            double w = Math.max(nodeBounds.getWidth(), textBounds.getWidth());
            double h = Math.max(nodeBounds.getHeight(), textBounds.getHeight());
            nodeBounds.setRect(MathUtil.centeredRectangle(x, y, w, h));
            node.setActualBounds(nodeBounds);
        }
        g.dispose();
    }

    /**
     * Sets the text and font to the text and font of the first selected Node in the graph. If no Nodes are selected
     * then this text field is made invisible.
     */
    private void setProperties() {
        if (graph.getSelectedNodes().isEmpty())
            setVisible(false);
        else {
            setVisible(true);
            Node firstNode = graph.getSelectedNodes().get(0);
            setFont(firstNode.getActualFont().deriveFont(12.0f));
            setForeground(Color.BLACK);
            if (!getText().equals(firstNode.getActualName())) {
                //NOTE(Boris): We need to disable updates here - because setText() will trigger updateSelectedNodeText
                // even though all we actually wanted to do was set the text and not change the text of the nodes.
                shouldChangeNodes = false;
                setText(firstNode.getActualName());
                shouldChangeNodes = true;
            }
        }
    }
}