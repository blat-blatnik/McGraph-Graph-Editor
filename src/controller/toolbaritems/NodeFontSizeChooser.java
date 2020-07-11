package controller.toolbaritems;

import controller.NodeToolBar;
import model.Graph;
import model.Node;
import controller.animation.Animation;
import controller.animation.ColorBlinkAnimation;
import controller.undoableedits.NodeEdit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
* @version 1.4
 *
 * The user can use this control to change the font size of the selected Nodes in a graph.
 *
 * @see Node
 * @see NodeToolBar
 */
public class NodeFontSizeChooser extends JSpinner {

    private final Graph graph;
    private boolean shouldChangeNodes;

    /**
     * Constructs a NodeFontSizeChooser for the given graph.
     *
     * @param graph The Graph whose Nodes will have their font size change.
     */
    public NodeFontSizeChooser(Graph graph) {
        super(new SpinnerNumberModel(12, 1, 100, 1));
        setToolTipText("Change the font size of selected nodes.");

        this.graph = graph;
        shouldChangeNodes = true;

        setProperties();
        graph.addObserver((o, msg) -> setProperties());

        addChangeListener(e -> {
            if (!shouldChangeNodes)
                return;

            float newSize = (float)(int)(Integer)getValue();
            new NodeEdit(graph, graph.getSelectedNodes(), node -> {
                Font oldFont = node.getVisualFont();
                Font newFont = oldFont.deriveFont(newSize);
                node.setActualFont(newFont);
            });
        });


        MouseListener listener = new MouseAdapter() {
            private final Animation textColorAnimation = new ColorBlinkAnimation<>(
                    graph::getSelectedNodes, Node::getActualTextColor, Node::setVisualTextColor);

            @Override
            public void mouseEntered(MouseEvent e) {
                textColorAnimation.play();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                textColorAnimation.stop();
                textColorAnimation.setCurrentTime(0);
                for (Node node : graph.getSelectedNodes())
                    node.setVisualTextColor(node.getActualTextColor());
            }
        };

        //NOTE(Boris): This is a composite component so we need to add the mouse listener to every sub-component.
        addMouseListener(listener);
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)getEditor();
        editor.getTextField().addMouseListener(listener);
        for (Component component : getComponents())
            component.addMouseListener(listener);
    }

    /**
     * Set the value of this spinner to the font size of the first selected Node of the graph. If no Nodes are selected
     * in the graph, then the spinner is made invisible.
     */
    private void setProperties() {
        java.util.List<Node> selectedNodes = graph.getSelectedNodes();

        if (selectedNodes.isEmpty())
            setVisible(false);
        else {
            setVisible(true);
            Node firstNode = selectedNodes.get(0);
            shouldChangeNodes = false;
            setValue(firstNode.getActualFont().getSize());
            shouldChangeNodes = true;
        }
    }

}