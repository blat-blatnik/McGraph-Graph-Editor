package controller;

import controller.toolbaritems.*;
import model.Graph;
import model.Node;
import model.NodeStyle;
import utils.Diamond2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
* @version 1.9
 *
 * This ToolBar houses all of the controllers that manipulate existing Nodes from a GraphModel. The NodeToolBar is
 * invisible to the user until they select a Node from the GraphModel.
 *
 * @see NodeColorChooser
 * @see NodeStyleToggle
 * @see NodeTextField
 * @see NodeFontChooser
 * @see NodeFontSizeChooser
 * @see NodeFontPropertyToggle
 */
public class NodeToolBar extends JToolBar {

    /**
     * Constructs a new NodeToolBar for a given GraphModel.
     *
     * @param graph The GraphModel that this NodeToolBar will control.
     */
    public NodeToolBar(Graph graph) {
        super();

        setFloatable(false);
        setVisible(false);



        add(new NodeColorChooser(graph, "Set the background color of selected nodes.",
                Node::getActualFillColor,
                Node::setActualFillColor,
                Node::setVisualFillColor));
        add(new NodeColorChooser(graph, "Set the border color of selected nodes.",
                Node::getActualBorderColor,
                Node::setActualBorderColor,
                Node::setVisualBorderColor));
        add(new NodeColorChooser(graph, "Set the text color of selected nodes.",
                Node::getActualTextColor,
                Node::setActualTextColor,
                Node::setVisualTextColor));

        addSeparator();

        add(new NodeStyleToggle(graph, NodeStyle.RECTANGLE, "Change the shape of all selected nodes to a rectangle.",
                (bounds) -> bounds));
        add(new NodeStyleToggle(graph, NodeStyle.ROUNDED_RECTANGLE, "Change the shape of all selected nodes to a rectangle with rounded edges.",
                (bounds) -> {
                    RoundRectangle2D roundRect = new RoundRectangle2D.Double();
                    roundRect.setRoundRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 8, 8);
                    return roundRect;
                }));
        add(new NodeStyleToggle(graph, NodeStyle.ELLIPSE, "Change the shape of all selected nodes to an ellipse.",
                (bounds) -> {
                    Ellipse2D ellipse = new Ellipse2D.Double();
                    ellipse.setFrame(bounds);
                    return ellipse;
                }));
        add(new NodeStyleToggle(graph, NodeStyle.DIAMOND, "Change the shape of all selected nodes to a diamond.",
                Diamond2D.Double::new));

        addSeparator();

        add(new NodeTextField(graph));

        addSeparator();

        add(new NodeFontChooser(graph));
        add(new NodeFontSizeChooser(graph));
        add(new NodeFontPropertyToggle(graph, "B", "Toggle bold font on all selected nodes.", Font.BOLD));
        add(new NodeFontPropertyToggle(graph, "I", "Toggle italic font on all selected nodes.", Font.ITALIC));

        graph.addObserver((obj, msg) -> {
            setVisible(graph.numSelectedNodes() > 0);
            if (graph.nodeWasClicked()) {
                requestFocusInWindow();
                graph.setNodeClicked(false);
            }
        });

    }

}