package controller;

import controller.toolbaritems.*;
import model.EdgeStyle;
import model.Graph;

import javax.swing.*;
import java.awt.geom.Path2D;

/**
* @version 1.9
 *
 * This ToolBar houses all the controllers used to manipulate existing Edges in a GraphModel. The EdgeToolBar is not
 * visible to the user until they select an Edge.
 *
 * @see EdgeColorChooser
 * @see EdgeDirectionButton
 * @see EdgeStyleToggle
 * @see EdgeWeightChooser
 */
public class EdgeToolBar extends JToolBar {

    /**
     * Constructs a new EdgeToolBar for the given GraphModel.
     *
     * @param graph The GraphModel whose Edges this toolbar will control.
     */
    public EdgeToolBar(Graph graph) {
        super();

        setFloatable(false);
        setVisible(false);
        graph.addObserver((obj, msg) -> setVisible(graph.numSelectedEdges() > 0));

        add(new EdgeColorChooser(graph));
        addSeparator();
        add(new EdgeDirectionButton(graph));
        addSeparator();
        add(new EdgeStyleToggle(graph, EdgeStyle.ELBOW_JOINT, "Elbow joint",
                (x0, y0, x1, y1) -> {
                    double xh = (x0 + x1) / 2;
                    Path2D.Float path = new Path2D.Float();
                    path.moveTo(x0, y0);
                    path.lineTo(xh, y0);
                    path.lineTo(xh, y1);
                    path.lineTo(x1, y1);
                    return path;
                }));
        add(new EdgeStyleToggle(graph, EdgeStyle.QUADRATIC_BEZIER, "Curve",
                (x0, y0, x1, y1) -> {
                    Path2D.Float path = new Path2D.Float();
                    path.moveTo(x0, y0);
                    path.quadTo(x0, y1, x1, y1);
                    return path;
                }));
        add(new EdgeStyleToggle(graph, EdgeStyle.CUBIC_BEZIER, "Wave",
                (x0, y0, x1, y1) -> {
                    double xh = (x0 + x1) / 2;
                    Path2D.Float path = new Path2D.Float();
                    path.moveTo(x0, y0);
                    path.curveTo(xh, y0, xh, y1, x1, y1);
                    return path;
                }));
        addSeparator();
        add(new EdgeWeightChooser(graph));
    }

}