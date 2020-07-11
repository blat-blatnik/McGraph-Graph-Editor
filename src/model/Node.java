package model;

import utils.Diamond2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Observable;

/**
  * @version 10.0
 *
 * This is the class that stores all data of a node. A node has two references to data, once the actual data that
 * the node has and once the visual data, that is used for animations and should not change actual data.
 *
 * @see NodeData
 * @see NodeStyle
 * @see NodeBorders
 */
public class Node extends Observable {

    /**
     * The default color used to fill the node.
     */
    public static final Color DEFAULT_FILL_COLOR = new Color(149, 149, 149);

    /**
     * The default color used to color the border.
     */
    public static final Color DEFAULT_BORDER_COLOR = new Color(0, 0, 0);

    /**
     * The color used to color the text of a node.
     */
    public static final Color DEFAULT_TEXT_COLOR = new Color(255, 255, 255);

    /**
     * The font used for the text of a node.
     */
    public static final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    /**
     * The width of the node.
     */
    public static final double DEFAULT_WIDTH = 100;

    /**
     * The height of the node.
     */
    public static final double DEFAULT_HEIGHT = 20;

    /**
     * The style of a node, e.g. Rectangle.
     */
    public static final NodeStyle DEFAULT_STYLE = NodeStyle.RECTANGLE;

    /**
     * The default name of a node.
     */
    public static final String DEFAULT_NAME = "New Node";

    private NodeData visualData;
    private NodeData actualData;
    private NodeBorders selectedBorders;

    /**
     * Constructs a node for the graph without default values.
     *
     * @param name name of the node
     * @param x x coordinate of the node
     * @param y y coordinate of the node
     * @param width width of the node
     * @param height height of the node
     * @param style style of the node
     * @param fillColor the color the node is filled with
     * @param borderColor the color of the border
     * @param textColor the color of the text
     * @param font the font of the name
     */
    public Node(String name, double x, double y, double width, double height, NodeStyle style,
                Color fillColor, Color borderColor, Color textColor, Font font) {
        actualData = new NodeData();
        actualData.setName(name);
        actualData.getBounds().setRect(x, y, width, height);
        actualData.setStyle(style);
        actualData.setFillColor(fillColor);
        actualData.setBorderColor(borderColor);
        actualData.setTextColor(textColor);
        actualData.setFont(font);
        visualData = new NodeData(actualData);
        selectedBorders = NodeBorders.NONE;
    }

    /**
     * Constructs a node with default style, fill color, border color, text color and font.
     * @param name name the node will have
     * @param x coordinate of the node
     * @param y coordinate of the node
     * @param width width of the node
     * @param height height of the node
     */
    public Node(String name, double x, double y, double width, double height) {
        this(name, x, y, width, height, DEFAULT_STYLE, DEFAULT_FILL_COLOR, DEFAULT_BORDER_COLOR,
                DEFAULT_TEXT_COLOR, DEFAULT_FONT);
    }

    /**
     * Constructs a node with default style, fill color, border color, text color, height and font.
     * @param name name of the node
     * @param centerX the center x-coordinate of the node
     * @param centerY the center y-coordinate of the node
     */
    public Node(String name, double centerX, double centerY) {
        this(name, centerX - DEFAULT_WIDTH / 2, centerY - DEFAULT_HEIGHT / 2, DEFAULT_WIDTH,
                DEFAULT_HEIGHT);
    }

    /**
     * Constructs a node with default style, fill color, border color, text color, height, name and font.
     * @param centerX the center x-coordinate of the node
     * @param centerY the center y-coordinate of the node
     */
    public Node(double centerX, double centerY) {
        this(DEFAULT_NAME, centerX, centerY);
    }

    /**
     * Constructs a node with default style, fill color, border color, text color, height and font.
     * @param name name will be given to the node
     */
    public Node(String name) {
        this(name, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * This is a copy constructor for a node. It clones a node by copying over the data from
     * the other node.
     * @param other the other node that the data is copied from
     */
    public Node(Node other) {
        actualData = new NodeData(other.actualData);
        visualData = new NodeData(actualData);
        selectedBorders = NodeBorders.NONE;
    }

    /**
     * Sets the data of the current node to the data that is passed, i.e. sets
     * new data to the current node.
     * @param otherData the new data of the node
     */
    public void setData(NodeData otherData){
        actualData = new NodeData(otherData);
        visualData = new NodeData(actualData);

        setChanged();
        notifyObservers();
    }

    /**
     * Sets the visual bounds of the node, and is used for animations.
     * @param bounds the bounds it will be set to.
     */
    public void setVisualBounds(Rectangle2D bounds) {
        if (bounds != null && !visualData.getBounds().equals(bounds)) {
            visualData.getBounds().setRect(bounds);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the actual bounds to new bounds.
     * @param bounds new bounds it will be set to
     */
    public void setActualBounds(Rectangle2D bounds) {
        if (bounds != null && !actualData.getBounds().equals(bounds)) {
            actualData.getBounds().setRect(bounds);
            visualData.getBounds().setRect(bounds);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Modifies the actual bounds of the node.
     * @param deltaX the delta x the node will be moved by
     * @param deltaY the delta y the node will be moved by
     * @param deltaWidth the width the node will be set to
     * @param deltaHeight the height the node will be set to
     */
    public void modifyBounds(double deltaX, double deltaY, double deltaWidth, double deltaHeight) {
        Rectangle2D.Double bounds = new Rectangle2D.Double();
        bounds.setRect(getActualBounds());
        bounds.x += deltaX;
        bounds.y += deltaY;
        bounds.width += deltaWidth;
        bounds.height += deltaHeight;

        if (bounds.width < 0) {
            bounds.width = -bounds.width;
            bounds.x -= bounds.width;
            if (selectedBorders.isLeft())
                selectedBorders = selectedBorders.setRight();
            else if (selectedBorders.isRight())
                selectedBorders = selectedBorders.setLeft();
        }

        if (bounds.height < 0) {
            bounds.height = -bounds.height;
            bounds.y -= bounds.height;
            if (selectedBorders.isTop())
                selectedBorders = selectedBorders.setBottom();
            else if (selectedBorders.isBottom())
                selectedBorders = selectedBorders.setTop();
        }

        setActualBounds(bounds);
    }

    /**
     * Moves the node to new coordinates.
     * @param newX new x coordinate.
     * @param newY new y coordinate.
     */
    public void moveTo(double newX, double newY) {
        Rectangle2D bounds = getActualBounds();
        bounds.setRect(newX, newY, bounds.getWidth(), bounds.getHeight());
        setActualBounds(bounds);
    }

    /**
     * Moves the node by deltaX and deltaY.
     * @param deltaX number the x will be moved
     * @param deltaY number the y will be moved
     */
    public void moveBy(double deltaX, double deltaY) {
        moveTo(actualData.getBounds().x + deltaX, actualData.getBounds().y + deltaY);
    }

    /**
     * Gets the visual style/shape of the node.
     * @return new shape of the node
     */
    public Shape getDrawableShape() {
        Rectangle2D bounds = getVisualBounds();
        switch (getVisualStyle()) {
            case RECTANGLE:
                return bounds;
            case ROUNDED_RECTANGLE:
                RoundRectangle2D.Double roundRect = new RoundRectangle2D.Double();
                roundRect.setRoundRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 10, 10);
                return roundRect;
            case ELLIPSE:
                Ellipse2D.Double ellipse = new Ellipse2D.Double();
                ellipse.setFrame(bounds);
                return ellipse;
            case DIAMOND:
                return new Diamond2D.Double(bounds);
            default:
                return null; // NOTE: This should never happen.
        }
    }

    /**
     * Gets the visual bounds of the node.
     * @return visual bounds
     */
    public Rectangle2D getVisualBounds() {
        return visualData.getBounds().getBounds2D();
    }

    /**
     * Gets actual bounds of the node.
     * @return actual bounds of the node
     */
    public Rectangle2D getActualBounds() {
        return actualData.getBounds().getBounds2D();
    }

    /**
     * Gets the visual name of the node.
     * @return visual name
     */
    public String getVisualName() {
        return visualData.getName();
    }

    /**
     * Gets the actual name of the node.
     * @return actual name
     */
    public String getActualName() {
        return actualData.getName();
    }

    /**
     * Sets the actual name of the node.
     * @param newName new name it will be set to
     */
    public void setActualName(String newName) {
        if (newName != null && !actualData.getName().equals(newName)) {
            actualData.setName(newName);
            setChanged();
            notifyObservers();
        }
        visualData.setName(newName);

    }

    /**
     * Gets the visual fill color.
     * @return visual fill color
     */
    public Color getVisualFillColor() {
        return visualData.getFillColor();
    }

    /**
     * Gets the actual fill color.
     * @return actual fill color
     */
    public Color getActualFillColor() {
        return actualData.getFillColor();
    }

    /**
     * Sets the visual fill color of the node.
     * @param newColor new color of the node
     */
    public void setVisualFillColor(Color newColor) {
        if (newColor != null && !visualData.getFillColor().equals(newColor)) {
            visualData.setFillColor(newColor);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the actual fill color of the node.
     * @param newColor new color of the node
     */
    public void setActualFillColor(Color newColor) {
        if (newColor != null && !actualData.getFillColor().equals(newColor)) {
            actualData.setFillColor(newColor);
            setChanged();
            notifyObservers();
        }
        setVisualFillColor(newColor);
    }

    /**
     * Gets the visual border color of the node.
     */
    public Color getVisualBorderColor() {
        return visualData.getBorderColor();
    }

    /**
     * Sets the visual fill color of the node.
     * @return border color new color of the node
     */
    public Color getActualBorderColor() {
        return actualData.getBorderColor();
    }

    /**
     * Sets the visual border color of the node.
     * @param newColor new color of the node
     */
    public void setVisualBorderColor(Color newColor) {
        if (newColor != null && !visualData.getBorderColor().equals(newColor)) {
            visualData.setBorderColor(newColor);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the actual fill color of the node.
     * @param newColor new color of the node
     */
    public void setActualBorderColor(Color newColor) {
        if (newColor != null && !actualData.getBorderColor().equals(newColor)) {
            actualData.setBorderColor(newColor);
            setChanged();
            notifyObservers();
        }
        setVisualBorderColor(newColor);
    }

    /**
     * Gets the visual text color of the node.
     */
    public Color getVisualTextColor() {
        return visualData.getTextColor();
    }

    /**
     * Gets the actual text color of the node.
     * @return newColor new color of the node text
     */
    public Color getActualTextColor() {
        return actualData.getTextColor();
    }

    /**
     * Sets the visual text color of the node.
     * @param newColor new color of the node text
     */
    public void setVisualTextColor(Color newColor) {
        if (newColor != null && !visualData.getTextColor().equals(newColor)) {
            visualData.setTextColor(newColor);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the actual fill color of the node.
     * @param newColor new color of the node text
     */
    public void setActualTextColor(Color newColor) {
        if (newColor != null && !actualData.getTextColor().equals(newColor)) {
            actualData.setTextColor(newColor);
            setChanged();
            notifyObservers();
        }
        setVisualTextColor(newColor);
    }

    /**
     * Sets the visual font of the node.
     * @return font of the node
     */
    public Font getVisualFont() {
        return visualData.getFont();
    }

    /**
     * Gets the actual font of the node.
     * @return font of the node
     */
    public Font getActualFont() {
        return actualData.getFont();
    }

    /**
     * Sets the visual font of the node.
     * @param newFont new font of the node
     */
    public void setVisualFont(Font newFont) {
        if (newFont != null && !visualData.getFont().equals(newFont)) {
            visualData.setFont(newFont);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the actual font of the node.
     * @param newFont new font it will be set to
     */
    public void setActualFont(Font newFont) {
        if (newFont != null && !actualData.getFont().equals(newFont)) {
            actualData.setFont(newFont);
            setChanged();
            notifyObservers();
        }
        setVisualFont(newFont);
    }

    /**
     * Gets the visual style of the node.
     * @return style of the node
     */
    public NodeStyle getVisualStyle() {
        return visualData.getStyle();
    }

    /**
     * Gets the actual style of the node.
     * @return style of the node
     */
    public NodeStyle getActualStyle() {
        return actualData.getStyle();
    }

    /**
     * Sets the visual style of the node.
     * @param newStyle new style of the node
     */
    public void setVisualStyle(NodeStyle newStyle) {
        if (newStyle != null && visualData.getStyle() != newStyle) {
            visualData.setStyle(newStyle);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the actual style of the node.
     * @param newStyle new style of the node
     */
    public void setActualStyle(NodeStyle newStyle) {
        if (newStyle != null && actualData.getStyle() != newStyle) {
            actualData.setStyle(newStyle);
            setChanged();
            notifyObservers();
        }
        setVisualStyle(newStyle);
    }

    /**
     * Gets the selected border of the node.
     * @return selected border
     */
    public NodeBorders getSelectedBorders() {
        return selectedBorders;
    }

    /**
     * Sets the selected borders of the node.
     * @param border that will be selected
     */
    public void setSelectedBorders(NodeBorders border) {
        if (selectedBorders != border) {
            selectedBorders = border;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Gets the actual data.
     * @return actual data
     */
    public NodeData getActualData(){
        return actualData;
    }


    @Override
    public String toString() {
        return "Node{" +
                "name=" + actualData.getName() +
                '}';
    }
}