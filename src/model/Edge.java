package model;

import utils.MathUtil;

import java.awt.*;
import java.awt.geom.*;
import java.util.Observable;
import java.util.Observer;
import java.util.List;

/**
 * @author Jana
 * @author Boris
 * @version 10.0
 *
 * This class is responsible to store all data related to an edge of the graph. An edge is connecting two nodes,
 * at this point is not cyclic, thus cannot connect to itself, and has different shapes, directions, colors and weights.
 *
 * @see Node
 * @see EdgeData
 * @see EdgeDirection
 * @see EdgeStyle
 */
public class Edge extends Observable implements Observer {

    /**
     * The default color of an edge.
     */
    public static final Color DEFAULT_COLOR = new Color(82, 82, 82);

    /**
     * The default weight of an edge.
     */
    public static final double DEFAULT_WEIGHT = 1.0;

    /**
     * The default font of an edge.
     */
    public static final Font DEFAULT_FONT = new Font(Font.DIALOG, Font.PLAIN, 6);

    private EdgeData actualData;
    private EdgeData visualData;

    private Node node1;
    private Node node2;
    private double arrow1Angle;
    private double arrow2Angle;
    private Point2D arrow1Point;
    private Point2D arrow2Point;
    private Line2D line;
    private Path2D path;

    /**
     * Constructing an edge with all necessary information given to it, without default values.
     * @param node1 first node it connects to
     * @param node2 first node it connects to
     * @param weight the weight of the edge
     * @param direction direction of the edge
     * @param style style or shape of the edge
     * @param color color of the edge
     *
     * @see Node
     * @see EdgeDirection
     * @see EdgeStyle
     * @see Color
     */
    public Edge(Node node1, Node node2, double weight, EdgeDirection direction, EdgeStyle style, Color color) {

        if (node1 == node2)
            throw new RuntimeException("Tried to create a cyclic Edge");

        this.node1 = node1;
        this.node2 = node2;

        actualData = new EdgeData();
        actualData.setWeight(weight);
        actualData.setDirection(direction);
        actualData.setStyle(style);
        actualData.setColor(color);
        actualData.setWeightPoint(new Point2D.Double());
        visualData = new EdgeData(actualData);

        calculateLine();
        actualData.getWeightPoint().setLocation(MathUtil.centerPoint(line));
        visualData.getWeightPoint().setLocation(actualData.getWeightPoint());
        calculatePath();

        node1.addObserver(this);
        node2.addObserver(this);
    }

    /**
     * Constructs a node with some default information (weight and style).
     * @param node1 first node the edge connects to
     * @param node2 second node the edge connects to
     * @param direction direction of the edge
     * @param color color of the edge
     *
     * @see Node
     * @see EdgeDirection
     * @see EdgeStyle
     * @see Color
     */
    public Edge(Node node1, Node node2, EdgeDirection direction, Color color) {
        this(node1, node2, DEFAULT_WEIGHT, direction, EdgeStyle.CUBIC_BEZIER, color);
    }

    /**
     * Constructs a node with some default information (weight, style, direction and color).
     * @param node1 first node the edge connects to
     * @param node2 second node the edge connects to
     */
    public Edge(Node node1, Node node2) {
        this(node1, node2, EdgeDirection.DIRECTED_TO_NODE_2, DEFAULT_COLOR);
    }

    /**
     * This is a copy constructor. It copies all data from an edge and instantiates a new edge. In that way,
     * edges can be cloned. This can be used in undo and redo functionality.
     * @param other the other node, the data will be copied from
     *
     * @see EdgeData
     */
    public Edge(Edge other) {
        //NOTE(Boris): This doesn't copy everything from the other Edge but that's ok - we don't want to copy over stuff
        // like the line and edge anyway..
        this(other.node1, other.node2);
        actualData = new EdgeData(other.actualData);
        visualData = new EdgeData(actualData);
        moveActualWeightPointTo(other.actualData.getWeightPoint().getX(), other.actualData.getWeightPoint().getY());
    }

    /**
     * Sets the data of the node to other data.
     * @param otherData the other data that will be copied
     * @see EdgeData
     */
    public void setData(EdgeData otherData) {
        actualData = new EdgeData(otherData);
        visualData = new EdgeData(actualData);
        calculateLine();
        calculatePath();
        setChanged();
        notifyObservers();
    }

    /**
     * Overrides the update-method of the observer interface. Updates the edge when being notified.
     * In this case if re-calculates the line, when it is notified by the node that the node has changed.
     * @param o observable
     * @param arg a generic message passed to the observer
     *
     * @see Observable
     * @see Object
     */
    @Override
    public void update(Observable o, Object arg) {

        Line2D oldLine = line;
        calculateLine();
        Line2D newLine = line;

        if (!newLine.equals(oldLine)) {

            if (MathUtil.length(oldLine) == 0) //NOTE(Boris): If old line is degenerate then just replace everything.
                actualData.getWeightPoint().setLocation(MathUtil.centerPoint(newLine));
            else {
                Point2D newPoint = MathUtil.transferPointBetweenLines(actualData.getWeightPoint(), oldLine, newLine);
                actualData.getWeightPoint().setLocation(newPoint);
            }

            // setting also visual data
            visualData.getWeightPoint().setLocation(actualData.getWeightPoint());
            calculatePath();

        }

        setChanged();
        notifyObservers();
    }

    /**
     * Calculates the straight line between two nodes.
     */
    private void calculateLine() {
        Rectangle2D bounds1 = node1.getVisualBounds();
        Rectangle2D bounds2 = node2.getVisualBounds();

        if (isDirectedToNode1())
            bounds1 = MathUtil.growRectangle(bounds1, 15, 15);
        if (isDirectedToNode2())
            bounds2 = MathUtil.growRectangle(bounds2, 15, 15);
        line = MathUtil.lineBetween(bounds1, bounds2);
    }

    /**
     * Calculates the path of the edge.
     */
    private void calculatePath() {

        switch (visualData.getStyle()) {
            case ELBOW_JOINT:
                Rectangle2D bounds1 = node1.getVisualBounds();
                Rectangle2D bounds2 = node2.getVisualBounds();
                if (isDirectedToNode1())
                    bounds1 = MathUtil.growRectangle(bounds1, 15, 15);
                if (isDirectedToNode2())
                    bounds2 = MathUtil.growRectangle(bounds2, 15, 15);
                path = MathUtil.elbowJointThrough(bounds1, actualData.getWeightPoint(), bounds2);
                break;
            case QUADRATIC_BEZIER:
                if (MathUtil.distanceBetween(actualData.getWeightPoint(), line) > 1)
                    path = new Path2D.Float(MathUtil.quadraticCurveThrough(line.getP1(), actualData.getWeightPoint(), line.getP2()));
                else
                    path = new Path2D.Float(line);
                break;
            default:
            case CUBIC_BEZIER:
                if (MathUtil.distanceBetween(actualData.getWeightPoint(), line) > 1) {
                    Point2D p1 = line.getP1();
                    Point2D p2 = MathUtil.centerPoint(new Line2D.Double(line.getP1(), MathUtil.centerPoint(line)));
                    Point2D p3 = actualData.getWeightPoint();
                    Point2D p4 = MathUtil.centerPoint(new Line2D.Double(line.getP2(), MathUtil.centerPoint(line)));
                    Point2D p5 = line.getP2();
                    path = MathUtil.cubicSplineThrough(p1, p2, p3, p4, p5);
                } else
                    path = new Path2D.Float(line);
                break;
        }

        List<Double> angles = MathUtil.pathSegmentAngles(path);
        List<Point2D> points = MathUtil.pathSegmentPoints(path);
        arrow1Angle = angles.get(0);
        arrow2Angle = angles.get(angles.size() - 1);
        arrow1Point = points.get(0);
        arrow2Point = points.get(points.size() - 1);
    }

    /**
     * Move actual weight point to a new point. Visual will be set automatically too.
     */
    public void moveActualWeightPointTo(double x, double y) {
        if (x != actualData.getWeightPoint().getX() || y != actualData.getWeightPoint().getY()) {
            actualData.getWeightPoint().setLocation(x, y);
            visualData.getWeightPoint().setLocation(x, y);
            calculatePath();
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Move actual weight point by x and y. Visual will be set automatically too.
     */
    public void moveActualWeightPointBy(double deltaX, double deltaY) {
        moveActualWeightPointTo(actualData.getWeightPoint().getX() + deltaX, actualData.getWeightPoint().getY() + deltaY);
    }

    /**
     * Gets the left node the edge is connected to.
     * @return left node
     */
    public Node getLeftNode() {
        if (node1.getVisualBounds().getCenterX() <= node2.getVisualBounds().getCenterX())
            return node1;
        else
            return node2;
    }

    /**
     * Gets the right node the edge is connected to.
     * @return right node
     * @see Node
     */
    public Node getRightNode() {
        if (node1.getVisualBounds().getCenterX() > node2.getVisualBounds().getCenterX())
            return node1;
        else
            return node2;
    }

    /**
     * Gets the other node the edge is connected to, given a node as a parameters.
     *
     * @param node that is given to determine the other nodes.
     * @return other node
     *
     * @see Node
     */
    public Node getOtherNode(Node node) {
        if (node == node1)
            return node2;
        if (node == node2)
            return node1;

        throw new IllegalArgumentException("getOtherNode() called with node that Edge doesn't connect to.");
    }

    /**
     * Gets the first node the edge is connected to.
     * @return first node
     * @see Node
     */
    public Node getNode1() {
        return node1;
    }

    /**
     * Gets the second node the edge is connected to.
     * @return second node
     * @see Node
     */
    public Node getNode2() {
        return node2;
    }

    /**
     * Sets the first node of the edge.
     * @param node that the edge will be set to
     * @see Node
     */
    public void setNode1(Node node) {
        if (node1 != node) {
            node1.deleteObserver(this);
            node1 = node;
            calculateLine();
            calculatePath();
            node1.addObserver(this);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the second node of the edge.
     * @param node that the edge will be set to
     * @see Node
     */
    public void setNode2(Node node) {
        if (node2 != node) {
            node2.deleteObserver(this);
            node2 = node;
            calculateLine();
            calculatePath();
            node2.addObserver(this);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Tests whether the edge is connected to the given node or not.
     *
     * @param node that is tested for
     * @return boolean indicating whether the edge is connecting to that node
     * @see Node
     */
    public boolean connectsTo(Node node) {
        return node == node1 || node == node2;
    }

    /**
     * Tests whether the edge is directed to the first node.
     * @return boolean indicating whether the edge is directed to the first node
     * @see Node
     */
    public boolean isDirectedToNode1() {
        return actualData.getDirection().directsToNode1();
    }

    /**
     * Tests whether the edge is directed to the first node.
     * @return boolean indicating whether the edge is directed to the first node
     * @see Node
     */
    public boolean isDirectedToNode2() {
        return actualData.getDirection().directsToNode2();
    }

    /**
     * Tests whether the edge is directed to the given node.
     * @param node that the edge is tested for
     * @return boolean indicating whether the edge is directed to that node
     * @see Node
     */
    public boolean isDirectedTo(Node node) {
        if (node == node1)
            return isDirectedToNode1();
        if (node == node2)
            return isDirectedToNode2();
        return false;
    }

    /**
     * Gets the actual data of the edge.
     * @return actual data.
     */
    public EdgeData getActualData(){
        return actualData;
    }

    /**
     * Gets the actual direction of the edge.
     * @return direction of the edge
     * @see EdgeDirection
     */
    public EdgeDirection getActualDirection() {
        return actualData.getDirection();
    }

    /**
     * Sets the actual direction of the edge.
     * @param newDirection new direction of the edge
     * @see EdgeDirection
     */
    public void setActualDirection(EdgeDirection newDirection) {
        if (actualData.getDirection() == newDirection) return;

        actualData.setDirection(newDirection);
        calculateLine();
        calculatePath();
        setChanged();
        notifyObservers();

        setVisualDirection(newDirection);
    }

    /**
     * Sets the visual direction of the edge for animation purposes.
     * @param newDirection new direction of the edge
     * @see EdgeDirection
     */
    public void setVisualDirection(EdgeDirection newDirection) {
        if (actualData.getDirection() == newDirection) return;

        visualData.setDirection(newDirection);
        calculateLine();
        calculatePath();
        setChanged();
        notifyObservers();

    }

    /**
     * Gets the actual color of the edge.
     * @return color
     */
    public Color getActualColor() {
        return actualData.getColor();
    }

    /**
     * Gets the visual color of the edge.
     * @return color
     */
    public Color getVisualColor() {
        return visualData.getColor();
    }

    /**
     * Sets the actual color of the edge. Once the user is done changing the edge,
     * the actual data is set again.
     * @param newColor The new actual color to set the Edge to.
     */
    public void setActualColor(Color newColor) {
        if (actualData.getColor().equals(newColor)) return;

        actualData.setColor(newColor);
        visualData.setColor(newColor);
        setChanged();
        notifyObservers();
    }

    /**
     * Sets the visual color to a new color. Used for animating a change of color,
     * before the user actually changes the color of the edge.
     * @param newColor the new color of the edge.
     */
    public void setVisualColor(Color newColor) {
        if (visualData.getColor().equals(newColor)) return;

        visualData.setColor(newColor);
        setChanged();
        notifyObservers();
    }

    /**
     * @return The actual weight of this Edge.
     */
    public double getActualWeight() {
        return actualData.getWeight();
    }

    /**
     * @return The visual weight used for animation purposes.
     */
    public double getVisualWeight() {
        return visualData.getWeight();
    }

    /**
     * Sets the actual weight of the edge.
     *
     * @param newWeight The new actual weight of this Edge.
     */
    public void setActualWeight(double newWeight) {
        if (actualData.getWeight() != newWeight) {
            actualData.setWeight(newWeight);
            visualData.setWeight(newWeight);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Gets the actual style of an edge.
     * @return style
     * @see EdgeStyle
     */
    public EdgeStyle getActualStyle() {
        return actualData.getStyle();
    }

    /**
     * Sets the actual style of an edge to a new one. Automatically sets visual style too.
     * @param newStyle new style it will be set to.
     * @see EdgeStyle
     */
    public void setActualStyle(EdgeStyle newStyle) {
        if (actualData.getStyle() != newStyle) {
            actualData.setStyle(newStyle);
            visualData.setStyle(newStyle);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the visual style of a color, for animation purposes.
     * @param newStyle new style it will be set to.
     * @see EdgeStyle
     */
    public void setVisualStyle(EdgeStyle newStyle) {
        if (visualData.getStyle() != newStyle) {
            visualData.setStyle(newStyle);
            calculateLine();
            calculatePath();
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Gets the actual point where the weight of the edge is.
     * @return point of the weight
     * @see Point2D
     */
    public Point2D getActualWeightPoint() {
        return actualData.getWeightPoint();
    }

    /**
     * Gets the visual point where the weight is.
     * @return visual point
     * @see Point2D
     */
    public Point2D getVisualWeightPoint() {
        return visualData.getWeightPoint();
    }

    /**
     * @return The straight Line connecting node1 and node2 of this edge.
     * @see Line2D
     */
    public Line2D getLine() {
        return line;
    }

    /**
     * @return The Path connecting node1 and node2 of this edge.
     * @see Path2D
     */
    public Path2D getPath() {
        return path;
    }

    /**
     * @return the angle of the first arrow.
     */
    public double getArrow1Angle() {
        return arrow1Angle;
    }

    /**
     * @return the angle of the second arrow.
     */
    public double getArrow2Angle() {
        return arrow2Angle;
    }

    /**
     * @return the point of the first arrow.
     */
    public Point2D getArrow1Point() {
        return arrow1Point;
    }

    /**
     * @return the point of the first arrow.
     */
    public Point2D getArrow2Point() {
        return arrow2Point;
    }
}