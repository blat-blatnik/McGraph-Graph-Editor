package controller.toolbaritems;

import controller.undoableedits.EdgeEdit;
import model.Edge;
import model.Graph;
import controller.animation.Animation;
import controller.animation.ColorBlinkAnimation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
* @version 1.3
 *
 * This control allows the user to select a new color for Edges. It also plays a ColorBlinkAnimation for Edge colors
 * when the user hovers their mouse over the button.
 *
 * @see Edge
 * @see Graph
 * @see ColorBlinkAnimation
 * @see controller.EdgeToolBar
 */
public class EdgeColorChooser extends JButton {

    private final Graph graph;

    /**
     * Constructs a EdgeColorChooser for the given graph.
     *
     * @param graph The GraphModel whose selected Edges will have their colors changed.
     */
    public EdgeColorChooser(Graph graph) {
        super();
        setToolTipText("Choose edge color.");
        setFocusPainted(false);

        this.graph = graph;

        setProperties();
        setIcon(makeIcon());
        graph.addObserver((o, msg) -> setProperties());

        addActionListener(e -> {
            List<Edge> selectedEdges = graph.getSelectedEdges();
            if (selectedEdges.isEmpty()) return;

            Color oldColor = selectedEdges.get(0).getActualColor();
            Color newColor = JColorChooser.showDialog(null, "Choose a color", oldColor);

            if (newColor == null) return;

            new EdgeEdit(graph, selectedEdges, edge -> edge.setActualColor(newColor));
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
                for (Edge edge : graph.getEdges())
                    edge.setVisualColor(edge.getActualColor());
            }
        });
    }

    /**
     * Enables or disables this control based on whether there are any selected Edges in the graph.
     */
    public void setProperties() {
        setVisible(graph.numSelectedEdges() >= 0);
    }

    /**
     * @return A new Icon for the EdgeColorChooser whose color matches the current color of the selected Edges.
     */
    private Icon makeIcon() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics graphics, int x, int y) {
                if (graph.numSelectedEdges() > 0) {
                    Edge firstEdge = graph.getSelectedEdges().get(0);
                    Color color = firstEdge.getActualColor();
                    Graphics2D g = (Graphics2D)graphics.create();
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setColor(color);
                    g.fill(new Rectangle2D.Double(x, y, getIconWidth(), getIconHeight()));
                    g.dispose();
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