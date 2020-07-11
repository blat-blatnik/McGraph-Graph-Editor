package controller.toolbaritems;

import controller.undoableedits.EdgeEdit;
import model.*;
import utils.ListUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Boris
 * @version 2.1
 *
 * This control allows the user toggle the EdgeStyle of all selected Edges to a particular variant. The style that this
 * control will toggle is a parameter of the constructor so that this control can be reused for all EdgeStyles. When
 * the user hovers their mouse over the button, the selected Edges will temporarily change style in order to indicate
 * to the user what the new style will look like, and which Edges will be changed.
 *
 * @see EdgeStyle
 * @see Edge
 * @see controller.EdgeToolBar
 */
public class EdgeStyleToggle extends JToggleButton {

    /**
     * This functional interface allows one to get an arbitrary path that fits into a rectangle for painting the Icon
     * of this control.
     */
    public interface PathGetter {
        /**
         * Returns a path that fits into the rectangle whose top-left corner is at (x0, x0) and bottom-left corner is
         * at (x1, y1).
         *
         * @param x0 The x coordinate of the top-left corner of the bounding rectangle.
         * @param y0 The y coordinate of the top-left corner of the bounding rectangle.
         * @param x1 The x coordinate of the bottom-right corner of the bounding rectangle.
         * @param y1 The y coordinate of the bottom-right corner of the bounding rectangle.
         * @return A Path2D that will be used to paint the Icon of this EdgeStyleToggle, that should fit into the rectangle.
         */
        Path2D getPath(double x0, double y0, double x1, double y1);
    }

    private final Graph graph;
    private final EdgeStyle styleToToggle;
    private boolean shouldChangeEdges;

    /**
     * Constructs an EdgeStyleToggle for a particular style and graph, with an appropriate tool tip text and a path
     * getter interface that will be used to paint the icon.
     *
     * @param graph The GraphModel whose Edges will be modified.
     * @param styleToToggle The EdgeStyle that this button will set on the selected Edges.
     * @param toolTipText The tooltip text that appears when the user hovers their mouse over this button.
     * @param pathGetter The PathGetter that will be used to paint the Icon of this button.
     */
    public EdgeStyleToggle(
            Graph graph,
            EdgeStyle styleToToggle,
            String toolTipText,
            PathGetter pathGetter)
    {
        super();
        setToolTipText(toolTipText);
        setIcon(makeIcon(pathGetter));

        this.graph = graph;
        this.styleToToggle = styleToToggle;
        shouldChangeEdges = true;

        setProperties();
        graph.addObserver((o, msg) -> setProperties());

        addActionListener(e -> {
            if (!shouldChangeEdges)
                return;
            new EdgeEdit(graph, graph.getSelectedEdges(), edge -> edge.setActualStyle(styleToToggle));
            setProperties();
        });

        addMouseListener(new MouseAdapter() {
            private final List<Edge> selectedEdges = new ArrayList<>();

            @Override
            public void mouseEntered(MouseEvent e) {
                selectedEdges.addAll(graph.getSelectedEdges());
                for (Edge edge : selectedEdges)
                    edge.setVisualStyle(styleToToggle);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                for (Edge edge : selectedEdges)
                    edge.setVisualStyle(edge.getActualStyle());
                selectedEdges.clear();
            }
        });
    }

    /**
     * Sets this button to be selected or deselected based on whether all selected Edges already have the
     * appropriate style. And makes the button invisible if there are no selected Edges.
     */
    public void setProperties() {
        List<Edge> selectedEdges = graph.getSelectedEdges();
        if (selectedEdges.isEmpty())
            setVisible(false);
        else {
            setVisible(true);
            boolean allEdgesHaveThisStyle = ListUtil.all(selectedEdges, edge -> edge.getActualStyle() == styleToToggle);
            shouldChangeEdges = false;
            setSelected(allEdgesHaveThisStyle);
            shouldChangeEdges = true;
        }
    }

    /**
     * @param pathGetter The PathGetter used to get a Path2D to paint to the new Icon.
     * @return An Icon painted with the path obtained from the PathGetter.
     */
    private static Icon makeIcon(PathGetter pathGetter) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics graphics, int x, int y) {

                double x0 = x + 1;
                double y0 = y + 1;
                double x1 = x0 + getIconWidth() - 2;
                double y1 = y0 + getIconHeight() - 2;
                Path2D path = pathGetter.getPath(x0, y0, x1, y1);

                Graphics2D g = (Graphics2D)graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Edge.DEFAULT_COLOR);
                g.setStroke(new BasicStroke(2));
                g.draw(path);
                g.dispose();
            }

            @Override
            public int getIconWidth() {
                return 24;
            }

            @Override
            public int getIconHeight() {
                return 16;
            }
        };
    }

}