package controller.toolbaritems;

import controller.undoableedits.EdgeEdit;
import model.Edge;
import model.EdgeDirection;
import model.Graph;
import model.Node;
import controller.animation.Animation;
import controller.animation.ColorBlinkAnimation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

/**
 * @author Boris
 * @version 2.0
 *
 * This control allows the user to change the directionality of the selected Edges. It also plays a ColorBlinkAnimation
 * when the user hovers over the button in order to indicate which Edges will change to the user. All the EdgeDirections
 * are cycled through.
 *
 * @see EdgeDirection
 * @see Edge
 * @see ColorBlinkAnimation
 * @see controller.EdgeToolBar
 */
public class EdgeDirectionButton extends JButton {

    private final Graph graph;

    /**
     * Constructs a EdgeDirectionButton for the specified graph.
     *
     * @param graph The GraphModel on whose selected Edges to cycle directionality.
     */
    public EdgeDirectionButton(Graph graph) {
        super();
        setToolTipText("Toggle the direction of the edge.");

        this.graph = graph;

        setProperties();
        setIcon(makeIcon());
        graph.addObserver((o, msg) -> setProperties());
        addActionListener(e -> {
            Edge firstEdge = graph.getSelectedEdges().get(0);
            EdgeDirection oldDirection = firstEdge.getActualDirection();
            EdgeDirection newDirection = EdgeDirection.DIRECTED_TO_NODE_1;

            switch (oldDirection) {
                case DIRECTED_TO_NODE_1:
                    newDirection = EdgeDirection.DIRECTED_TO_BOTH_NODES;
                    break;
                case DIRECTED_TO_BOTH_NODES:
                    newDirection = EdgeDirection.DIRECTED_TO_NODE_2;
                    break;
            }

            EdgeDirection finalNewDirection = newDirection;
            new EdgeEdit(graph, graph.getSelectedEdges(), edge -> edge.setActualDirection(finalNewDirection));
        });

        addMouseListener(new MouseAdapter() {
            private final Animation edgeColorAnimation = new ColorBlinkAnimation<>(
                    graph::getSelectedEdges, Edge::getActualColor, Edge::setVisualColor);

            @Override
            public void mouseEntered(MouseEvent e) {
                edgeColorAnimation.play();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                edgeColorAnimation.stop();
                edgeColorAnimation.setCurrentTime(0);
                for (Edge edge : graph.getSelectedEdges())
                    edge.setVisualColor(edge.getActualColor());
            }
        });
    }

    /**
     * Enables or disables this control based on whether there are any selected edges in the graph.
     */
    private void setProperties() {
        setVisible(graph.numSelectedEdges() > 0);
    }

    /**
     * @return A new Icon that shows the current directionality of the first selected Edge in the graph.
     */
    private Icon makeIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics graphics, int x, int y) {
                if (graph.numSelectedEdges() > 0) {

                    Edge firstEdge = graph.getSelectedEdges().get(0);
                    Node leftNode = firstEdge.getLeftNode();
                    Node rightNode = firstEdge.getRightNode();
                    double halfHeight = y + getIconHeight() / 2.0;
                    double x1 = x + 2;
                    double x2 = x + getIconWidth() - 2;

                    Graphics2D g = (Graphics2D)graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(Edge.DEFAULT_COLOR);
                    g.setStroke(new BasicStroke(2));
                    g.draw(new Line2D.Double(x1, halfHeight, x2, halfHeight));
                    if (firstEdge.isDirectedTo(leftNode)) {
                        g.draw(new Line2D.Double(x1, halfHeight, x1 + getIconWidth() / 6.0, halfHeight + 4));
                        g.draw(new Line2D.Double(x1, halfHeight, x1 + getIconWidth() / 6.0, halfHeight - 4));
                    }
                    if (firstEdge.isDirectedTo(rightNode)) {
                        g.draw(new Line2D.Double(x2, halfHeight, x2 - getIconWidth() / 6.0, halfHeight + 4));
                        g.draw(new Line2D.Double(x2, halfHeight, x2 - getIconWidth() / 6.0, halfHeight - 4));
                    }
                    g.dispose();
                }
            }

            @Override
            public int getIconWidth() {
                return 32;
            }

            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }

}