package controller.toolbaritems;

import model.*;
import controller.undoableedits.NodeEdit;
import utils.ListUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
* @version 2.0
 *
 * This control allows the user toggle the NodeStyle of all selected Nodes to a particular variant. The style that this
 * control will toggle is a parameter of the constructor so that this control can be reused for all NodeStyles. When
 * the user hovers their mouse over the button, the selected Nodes will temporarily change style in order to indicate
 * to the user what the new style will look like, and which Nodes will be changed.
 *
 * @see NodeStyle
 * @see Node
 * @see controller.NodeToolBar
 */
public class NodeStyleToggle extends JToggleButton {

    /**
     * This functional interface allows one to get an arbitrary shape that fits into a rectangle for painting the Icon
     * of this control.
     */
    public interface ShapeGetter {
        /**
         * @param shapeBounds The Rectangle2D into which the shape should fit.
         * @return A Shape that will be used to paint the Icon of this NodeStyleToggle, that should fit into the rectangle.
         */
        Shape getShape(Rectangle2D shapeBounds);
    }

    private final Graph graph;
    private final NodeStyle styleToToggle;
    private boolean shouldChangeNodes;

    /**
     * Constructs a NodeStyleToggle for the given graph, and with a given tooltip text. The style that this button
     * should toggle is also given, as well as a ShapeGetter used to get a shape to paint the icon with.
     *
     * @param graph The Graph whose Nodes to set the style of.
     * @param styleToToggle The NodeStyle that this button should toggle.
     * @param toolTipText The tooltip that will appear over this button when the user hovers their mouse over it.
     * @param shapeGetter The ShapeGetter that will be used to paint the Icon of this button.
     */
    public NodeStyleToggle(
            Graph graph,
            NodeStyle styleToToggle,
            String toolTipText,
            ShapeGetter shapeGetter)
    {
        super();
        setToolTipText(toolTipText);
        setIcon(makeIcon(shapeGetter));

        this.graph = graph;
        this.styleToToggle = styleToToggle;
        shouldChangeNodes = true;

        setProperties();
        graph.addObserver((o, msg) -> setProperties());

        addActionListener(e -> {
            if (!shouldChangeNodes)
                return;

            new NodeEdit(graph, graph.getSelectedNodes(), node -> node.setActualStyle(styleToToggle));
            setProperties();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                for (Node node : graph.getSelectedNodes())
                    node.setVisualStyle(styleToToggle);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                for (Node node : graph.getSelectedNodes())
                    node.setVisualStyle(node.getActualStyle());
            }
        });
    }

    /**
     * Changes whether the button is toggled based on whether all selected Nodes have the style that this button
     * toggles. If no Nodes are selected then makes this button invisible.
     */
    public void setProperties() {
        List<Node> selectedNodes = graph.getSelectedNodes();

        if (selectedNodes.isEmpty())
            setVisible(false);
        else {
            setVisible(true);
            boolean allNodesHaveThisStyle = ListUtil.all(selectedNodes, node -> node.getActualStyle() == styleToToggle);
            shouldChangeNodes = false;
            setSelected(allNodesHaveThisStyle);
            shouldChangeNodes = true;
        }
    }

    /**
     * @param shapeGetter The ShapeGetter that is used to get an appropriate Shape to draw in this Icon.
     * @return An Icon appropriate for the NodeStyle that this button should toggle.
     */
    private static Icon makeIcon(ShapeGetter shapeGetter) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics graphics, int x, int y) {

                Rectangle2D bounds = new Rectangle2D.Double(x + 1, y + 1, getIconWidth() - 2, getIconHeight() - 2);
                Shape shape = shapeGetter.getShape(bounds);

                Graphics2D g = (Graphics2D)graphics;
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Node.DEFAULT_FILL_COLOR);
                g.fill(shape);
                g.setColor(Node.DEFAULT_BORDER_COLOR);
                g.draw(shape);
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