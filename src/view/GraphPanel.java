package view;

import controller.EdgeToolBar;
import controller.NodeToolBar;
import controller.PopupMenu;
import model.Edge;
import model.Graph;
import model.Node;
import controller.Solver;
import utils.MathUtil;
import utils.TextUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Objects;

/**
 * @author Boris
 * @version 99.0
 *
 * This is the JPanel used to display a GraphModel. The GraphPanel also houses the NodeToolBar and the EdgeToolBar
 * controls.
 *
 * @see Graph
 * @see Node
 * @see Edge
 * @see NodeToolBar
 * @see EdgeToolBar
 * @see controller.animation.Animation
 */
public class GraphPanel extends JPanel {

    private static final Color SELECTION_RECTANGLE_EDGE_COLOR = new Color(0, 150, 150, 100);
    private static final Color SELECTION_RECTANGLE_FILL_COLOR = new Color(0, 150, 150, 50);
    private static final Polygon ARROW_HEAD = new Polygon(
            new int[]{ +7, +0, +0 }, // X's
            new int[]{ +0, +3, -3 }, // Y's
            3);

    private final Graph graph;
    private Rectangle2D selectionRectangle;
    private Rectangle2D boundsInGraphSpace;
    private double translationX;
    private double translationY;
    private double scale;

    /**
     * Constructs a new GraphPanel for the given GraphModel. This also constructs a NodeToolBar and an EdgeToolBar and
     * adds it to the panel. By default the GraphPanel has a centered FlowLayout.
     *
     * @param graph The GraphModel that this panel will view.
     */
    public GraphPanel(Graph graph) {
        super();
        this.graph = graph;

        selectionRectangle = null;
        translationX = 0.0;
        translationY = 0.0;
        scale = 1.0;

        setVisible(true);
        setOpaque(true);

        add(new NodeToolBar(graph));
        add(new EdgeToolBar(graph));
        setComponentPopupMenu(new PopupMenu(graph, this));

        //NOTE(Boris): This layout makes the NodeToolBar be on top of EdgeToolBar.
        setLayout(new FlowLayout(FlowLayout.CENTER, 999999, 5));

        graph.addObserver((object, arg) -> repaint());
    }

    /**
     * Sets the current selection rectangle to the specified rectangle.
     *
     * @param rect The rectangle whose area to draw as the selection rectangle.
     */
    public void setSelectionRectangle(Rectangle2D rect) {
        if (!Objects.equals(rect, selectionRectangle)) {
            if (rect == null)
                selectionRectangle = null;
            else {
                selectionRectangle = new Rectangle2D.Double();
                selectionRectangle.setRect(rect);
            }
            repaint();
        }
    }

    /**
     * @param pointInPanelSpace A point in panel space.
     * @return The given point projected to graph space.
     */
    public Point2D projectToGraphSpace(Point2D pointInPanelSpace) {
        double x = (pointInPanelSpace.getX() - translationX) / scale;
        double y = (pointInPanelSpace.getY() - translationY) / scale;
        return new Point2D.Double(x, y);
    }

    /**
     * @param rectInPanelSpace A rectangle in panel space.
     * @return The given rectangle completely projected to graph space.
     */
    public Rectangle2D projectToGraphSpace(Rectangle2D rectInPanelSpace) {
        Point2D topLeft = MathUtil.topLeft(rectInPanelSpace);
        topLeft = projectToGraphSpace(topLeft);
        double width = rectInPanelSpace.getWidth() / scale;
        double height = rectInPanelSpace.getHeight() / scale;
        return new Rectangle2D.Double(topLeft.getX(), topLeft.getY(), width, height);
    }

    /**
     * @return The current scale of this panel's view. To facilitate zooming in and out, all coordinates drawn by this panel are multiplied by the scale.
     */
    public double getScale() {
        return scale;
    }

    /**
     * Zooms into, or out of the center of the screen by a given factor.
     *
     * @param factor The factor that will multiply the current scale of this panel.
     */
    public void zoom(double factor) {
        zoom(factor, getWidth() / 2.0, getHeight() / 2.0);
    }

    /**
     * Zooms into, or out of a specified point on the screen by a given factor.
     *
     * @param factor The factor that will multiply the current scale of this panel.
     * @param stationaryX The x coordinate of the point that will map exactly to where it currently is after the scaling operation.
     * @param stationaryY The y coordinate of the point that will map exactly to where it currently is after the scaling operation.
     */
    public void zoom(double factor, double stationaryX, double stationaryY) {
        setScale(scale * factor, stationaryX, stationaryY);
    }

    /**
     * Sets this panel's translation to be exactly centered on the graph, and the panel's scale to be able to view the
     * entire graph at once.
     */
    public void centerViewOnGraph() {
        Rectangle2D bounds;

        if (graph.getNodes().isEmpty()) {
            bounds = getBounds();
        } else {
            bounds = graph.getBounds();
            double border = 0.05 * Math.min(getWidth(), getHeight());
            bounds.setRect(bounds.getX() - border, bounds.getY() - border,
                    bounds.getWidth() + 2 * border, bounds.getHeight() + 2 * border);
        }

        centerViewOn(bounds);
    }

    /**
     * Sets this panel's translation to be exactly centered on the given rectangle, and the panel's scale to be able to view the entire rectangle at once.
     *
     * @param target The Rectangle to center to panel's view on.
     */
    public void centerViewOn(Rectangle2D target) {
        double width  = getWidth();
        double height = getHeight();
        double targetWidth  = target.getWidth();
        double targetHeight = target.getHeight();
        double widthRatio  = width  / targetWidth;
        double heightRatio = height / targetHeight;

        scale = Math.min(widthRatio, heightRatio);
        translationX = -target.getX() * scale;
        translationY = -target.getY() * scale;
        translationX += ((width  - targetWidth  * scale) / 2);
        translationY += ((height - targetHeight * scale) / 2);

        repaint();
    }

    /**
     * Adds the given translation to the panel's current translation.
     *
     * @param dX The amount to modify the x translation of the panel.
     * @param dY The amount to modify the y translation of the panel.
     */
    public void translate(double dX, double dY) {
        if (dX != 0 || dY != 0) {
            translationX += dX;
            translationY += dY;
            repaint();
        }
    }

    /**
     * Repaints the entire panel with the Nodes, Edges of the GraphModel.
     *
     * @param graphics The Graphics object used to paint.
     */
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D)graphics.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(1));

        //NOTE(Boris): This means *first* scale and *then* translate.. ugh..
        g.translate(translationX, translationY);
        g.scale(scale, scale);

        //NOTE(Boris): Project the bounds to graph space to avoid having to project all Nodes and Edges to panel space.
        boundsInGraphSpace = projectToGraphSpace(getBounds());

        //NOTE(Boris): Paint edges first and *then* nodes - otherwise edges cover the nodes.
        paintEdges(g);
        paintNodes(g);

        paintCursorOverNode(g, graph.getStartNode(), Solver.START_COLOR, "START");
        paintCursorOverNode(g, graph.getGoalNode(), Solver.GOAL_COLOR, "GOAL");

        if (selectionRectangle != null) {
            g.setColor(SELECTION_RECTANGLE_FILL_COLOR);
            g.fill(selectionRectangle);
            g.setColor(SELECTION_RECTANGLE_EDGE_COLOR);
            g.draw(selectionRectangle);
        }

        if (graph.numSelectedNodes() > 0 || graph.numSelectedEdges() > 0) {
            int bottom = getHeight();
            g.setTransform(AffineTransform.getScaleInstance(1, 1));
            g.setFont(new Font(Font.DIALOG, Font.PLAIN, 11));
            g.setColor(Color.GRAY);
            if (graph.numSelectedNodes() > 0)
                g.drawString(graph.numSelectedNodes() + " selected node(s)", 20, bottom - 40);
            if (graph.numSelectedEdges() > 0)
                g.drawString(graph.numSelectedEdges() + " selected edge(s)", 20, bottom - 20);
        }

        g.dispose();
    }

    /**
     * Sets the scale of this panel's view to an exact value while keeping a certain specified point stationary.
     *
     * @param newScale The new scale of the panel's view.
     * @param stationaryX The x coordinate of the point that will map exactly to where it currently is after the scale changes.
     * @param stationaryY The y coordinate of the point that will map exactly to where it currently is after the scale changes.
     */
    private void setScale(double newScale, double stationaryX, double stationaryY) {
        if (newScale != scale) {
            // transform(x)         = (x * scale) + translation
            // inverse-transform(x) = (x - translation) / scale
            double sourceX = (stationaryX - translationX) / scale;
            double sourceY = (stationaryY - translationY) / scale;
            double newStationaryX = sourceX * newScale + translationX;
            double newStationaryY = sourceY * newScale + translationY;
            translationX += (stationaryX - newStationaryX);
            translationY += (stationaryY - newStationaryY);
            scale = newScale;
            repaint();
        }
    }

    /**
     * Paints all of the Nodes of the GraphModel.
     *
     * @param g The Graphics2D used to paint the Nodes.
     */
    private void paintNodes(Graphics2D g) {
        for (Node node : graph.getNodes())
            paintNode(g, node);
    }

    /**
     * Paints all of the Edges of the GraphModel, as well as the currently unfinished edges.
     *
     * @param g The Graphics2D used to paint the Nodes.
     */
    private void paintEdges(Graphics2D g) {
        for (Edge edge : graph.getEdges())
            paintEdge(g, edge);
        for (Edge edge : graph.getUnfinishedEdges())
            paintEdge(g, edge);
    }

    /**
     * Paints a single Node.
     *
     * @param g The Graphics2D object used to paint the Node.
     * @param node The Node to be painted.
     */
    private void paintNode(Graphics2D g, Node node) {

        Rectangle2D nodeBounds = node.getVisualBounds();

        //NOTE(Boris): Don't paint nodes that aren't visible.
        if (!nodeBounds.intersects(boundsInGraphSpace))
            return;

        Color color = node.getVisualFillColor();
        if (node == graph.getHoveredNode())
            color = MathUtil.darkerOrLighterColor(color, 0.2f);
        g.setColor(color);

        Stroke defaultStroke = g.getStroke();
        if (graph.isSelected(node))
            g.setStroke(new BasicStroke(2));

        Shape shape = node.getDrawableShape();
        g.fill(shape);
        g.setColor(node.getVisualBorderColor());
        if (scale > 0.3)
            g.draw(shape);

        if (graph.isSelected(node))
            g.setStroke(defaultStroke);

        Shape oldClip = g.getClip();
        g.setClip(shape);
        g.setColor(node.getVisualTextColor());
        g.setFont(node.getVisualFont());
        paintCenteredString(g, node.getVisualName(), nodeBounds.getCenterX(), nodeBounds.getCenterY());
        g.setClip(oldClip);
    }

    /**
     * Paints a single Edge.
     *
     * @param g The Graphics2D object used to paint the Edge.
     * @param edge The Edge to be painted.
     */
    private void paintEdge(Graphics2D g, Edge edge) {

        Line2D edgeLine = edge.getLine();
        Path2D edgePath = edge.getPath();

        //NOTE(Boris): Don't draw edges with degenerate lines.
        if (MathUtil.length(edgeLine) == 0)
            return;

        //NOTE(Boris): Don't bother drawing edges that aren't visible. We test the line because its much cheaper than
        // testing the whole path which may have like 5 bezier curves.
        if (!edgeLine.intersects(boundsInGraphSpace) && !edgePath.intersects(boundsInGraphSpace))
            return;

        Point2D edgeCenter = MathUtil.centerPoint(edgeLine);
        String weight = TextUtil.format(edge.getVisualWeight());
        Point2D weightPoint = edge.getVisualWeightPoint();

        g.setFont(Edge.DEFAULT_FONT);
        Rectangle2D textBounds = TextUtil.getStringBounds(g, weight);
        textBounds = MathUtil.centeredRectangle(edgeCenter, textBounds.getWidth(), textBounds.getHeight());
        textBounds = MathUtil.makeSquare(textBounds);
        float textSize = (float)(textBounds.getWidth());
        double textSizeOnScreen = textSize * scale;

        //NOTE(Boris): Only draw the weight string is its bigger than 5 pixels on the screen.
        if (textSizeOnScreen > 5) {
            g.setColor(Color.BLACK);
            paintCenteredString(g, weight, weightPoint.getX(), weightPoint.getY());
        }

        Color color = edge.getVisualColor();
        if (edge == graph.getHoveredEdge())
            color = MathUtil.darkerOrLighterColor(color, 0.2f);
        g.setColor(color);

        Stroke oldStroke = g.getStroke();
        Paint oldPaint = g.getPaint();
        if (graph.isSelected(edge))
            g.setStroke(new BasicStroke(2));

        //NOTE(Boris): Only use the RadialGradientPaint if the text is at least 5 pixels wide on the screen.
        if (textSizeOnScreen > 5) {
            float[] dist = { 0.5f, 1.0f };
            Color[] colors = { MathUtil.setAlpha(color, 0), color };
            RadialGradientPaint paint = new RadialGradientPaint(weightPoint, textSize, dist, colors);
            g.setPaint(paint);
        }

        g.draw(edgePath);

        g.setStroke(oldStroke);
        g.setPaint(oldPaint);

        Point2D arrow1Point = edge.getArrow1Point();
        Point2D arrow2Point = edge.getArrow2Point();
        double arrow1Angle = edge.getArrow1Angle();
        double arrow2Angle = edge.getArrow2Angle();

        AffineTransform oldTransform = g.getTransform();
        if (edge.isDirectedToNode1()) {
            g.translate(arrow1Point.getX(), arrow1Point.getY());
            g.rotate(arrow1Angle);
            g.fill(ARROW_HEAD);
            g.setTransform(oldTransform);
        }
        if (edge.isDirectedToNode2()) {
            g.translate(arrow2Point.getX(), arrow2Point.getY());
            g.rotate(arrow2Angle);
            g.fill(ARROW_HEAD);
            g.setTransform(oldTransform);
        }
    }

    /**
     * Paints a triangle cursor used to mark a node. This is used to mark the "start" and "goal" nodes for solving
     * operations.
     *
     * @param g The Graphics2D object used to paint the cursor.
     * @param node The Node over which the cursor should be painted.
     * @param color The Color of the cursor.
     * @param cursorText The text of the cursor.
     */
    private void paintCursorOverNode(Graphics2D g, Node node, Color color, String cursorText) {
        if (node == null)
            return;

        Rectangle2D bounds = node.getVisualBounds();
        Point2D topLineCenter = MathUtil.centerPoint(MathUtil.topEdge(bounds));

        AffineTransform oldTransform = g.getTransform();
        g.translate(topLineCenter.getX(), topLineCenter.getY() - 15);
        g.rotate(Math.PI / 2);
        g.scale(3, 4);

        g.setColor(color);
        g.fill(ARROW_HEAD);
        g.setColor(color.darker());
        g.draw(ARROW_HEAD);

        g.setTransform(oldTransform);
        g.setColor(Color.BLACK);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 6));
        paintCenteredString(g, cursorText, topLineCenter.getX(), topLineCenter.getY() - 8);
    }

    /**
     * Paints a given String centered on the specified coordinates using the currently set font.
     *
     * @param g The Graphics2D object used to paint the String.
     * @param string The String to paint.
     * @param x The x coordinate on which to center the painted String.
     * @param y The y coordinate on which to center the painted String.
     */
    private void paintCenteredString(Graphics2D g, String string, double x, double y) {
        FontMetrics metrics = g.getFontMetrics();
        Rectangle2D bounds = metrics.getStringBounds(string, g);
        double width = bounds.getWidth();
        double height = bounds.getHeight();

        //NOTE(Boris): Only draw string if its at least 3 pixels wide/tall on the screen.
        if (width * scale > 3 && height * scale > 3) {
            double startX = x - width  / 2.0;
            double startY = y - height / 2.0 + metrics.getAscent();
            g.drawString(string, (float)startX, (float)startY);
        }
    }
}