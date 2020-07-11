package controller.toolbaritems;

import model.Graph;
import model.Node;
import controller.animation.Animation;
import controller.animation.ColorBlinkAnimation;
import controller.undoableedits.NodeEdit;
import utils.Getter;
import utils.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
* @version 4.0
 *
 * This control can be used to choose a color for the selected Nodes in a graph. It is generic on how it retrieves and
 * sets colors on the Nodes, so it can be used for changing any colors of a Node, including text color, fill color and
 * border color. When the user hovers their mouse over this button, a ColorBlinkAnimation will play for the relevant
 * colors of the node in order to show the user which Nodes will be modified as well as which parts of the Node will
 * be modified.
 *
 * @see Node
 * @see controller.NodeToolBar
 * @see ColorBlinkAnimation
 */
public class NodeColorChooser extends JButton {

    private final Graph graph;
    private final Getter<Node, Color> actualColorGetter;

    /**
     * Constructs a NodeColorChooser for a given graph, with a given tooltip text, and with various color getters and
     * setters that can be used to get or set relevant colors on the Nodes.
     *
     * @param graph The GraphModel whose Nodes will have their color changed.
     * @param tooltipText The tooltip text that will appear when the user hovers their mouse over this button.
     * @param actualColorGetter The Getter used to get the relevant actual Color from a Node.
     * @param actualColorSetter The Setter used to set the relevant actual Color to a Node.
     * @param visualColorSetter The Setter used to set the relevant visual Color to a Node.
     */
    public NodeColorChooser(
            Graph graph,
            String tooltipText,
            Getter<Node, Color> actualColorGetter,
            Setter<Node, Color> actualColorSetter,
            Setter<Node, Color> visualColorSetter)
    {
        super();
        setToolTipText(tooltipText);
        setFocusPainted(false);

        this.graph = graph;
        this.actualColorGetter = actualColorGetter;

        graph.addObserver((o, msg) -> setProperties());

        setProperties();
        setIcon(makeIcon());

        addActionListener(e -> {
            List<Node> selectedNodes = graph.getSelectedNodes();
            if (selectedNodes.isEmpty())
                return;

            Node firstNode = selectedNodes.get(0);
            Color oldColor = actualColorGetter.get(firstNode);
            Color newColor = JColorChooser.showDialog(null, "Choose a color", oldColor);
            if (newColor != null) {
                new NodeEdit(graph, selectedNodes, node -> actualColorSetter.set(node, newColor));
            }
        });

        addMouseListener(new MouseAdapter() {
            private final Animation fillColorAnimation = new ColorBlinkAnimation<>(
                    graph::getSelectedNodes, actualColorGetter, visualColorSetter);

            @Override
            public void mouseEntered(MouseEvent e) {
                fillColorAnimation.play();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                fillColorAnimation.stop();
                fillColorAnimation.setCurrentTime(0);
                for (Node node : graph.getNodes())
                    visualColorSetter.set(node, actualColorGetter.get(node));
            }
        });
    }

    /**
     * Makes this button invisible if there are no selected nodes in the graph.
     */
    private void setProperties() {
        setVisible(graph.numSelectedNodes() > 0);
    }

    /**
     * @return A new Icon for the NodeColorChooser whose color matches the current color of the selected Nodes.
     */
    private Icon makeIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics graphics, int x, int y) {
                if (graph.numSelectedNodes() > 0) {
                    Node firstNode = graph.getSelectedNodes().get(0);
                    Color color = actualColorGetter.get(firstNode);
                    Graphics2D g = (Graphics2D)graphics;
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(color);
                    g.fill(new Rectangle2D.Double(x, y, getIconWidth(), getIconHeight()));
                }
            }

            @Override
            public int getIconWidth() {
                return 16;
            }

            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }

}