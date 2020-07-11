package model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

/**
 * @author Boris
 * @author Jana
 * @version 2.0
 *
 * Data class holds all data of the node. The edge will be able to have multiple
 * versions of this data, in order to have some only visual data and some actual
 * data. It can also be used in cloning an edge.
 *
 * @see Node
 * @see NodeStyle
 * @see Color
 * @see Point2D
 * @see Font
 * @see Rectangle2D
 */
public class NodeData {

    private String name       = "";
    private Font font         = Node.DEFAULT_FONT;
    private Color fillColor   = Node.DEFAULT_FILL_COLOR;
    private Color borderColor = Node.DEFAULT_BORDER_COLOR;
    private Color textColor   = Node.DEFAULT_TEXT_COLOR;
    private NodeStyle style   = Node.DEFAULT_STYLE;
    private Rectangle2D.Double bounds = new Rectangle2D.Double(0, 0, Node.DEFAULT_WIDTH, Node.DEFAULT_HEIGHT);

    /**
     * Constructs a data object without initial values. They can be assigned afterwards.
     */
    public NodeData() {}

    /**
     * Copy constructor: this can be used to clone data.
     * @param other the other data this data will be cloned from
     */
    public NodeData(NodeData other) {
        name = other.name;
        font = other.font;
        fillColor = other.fillColor;
        borderColor = other.borderColor;
        textColor = other.textColor;
        style = other.style;
        bounds = new Rectangle2D.Double();
        bounds.setRect(other.bounds);
    }

    /**
     * @return name stored in data.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name stored in data.
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return font stored in data.
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets the font stored in data.
     * @param font new font
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * @return fill color of the data.
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Sets the fill color stored in data.
     * @param fillColor new color to fill the node
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * @return border color stored in data.
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Sets border color stored in data.
     * @param borderColor new color for the border
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * @return text color stored in data.
     */
    public Color getTextColor() {
        return textColor;
    }

    /**
     * Sets the text color stored in data.
     * @param textColor new color
     */
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    /**
     * @return node style stored in data.
     * @see NodeStyle
     */
    public NodeStyle getStyle() {
        return style;
    }

    /**
     * Sets the style of the data.
     * @param style new style
     */
    public void setStyle(NodeStyle style) {
        this.style = style;
    }

    /**
     * @return the bounds of the data.
     * @see Rectangle2D
     */
    public Rectangle2D.Double getBounds() {
        return bounds;
    }

    /**
     * Overrides the equals method in order to compare whether two Data Objects
     * are logically the same, meaning all their fields and values correspond.
     *
     * @param o Object that this data is compared to.
     * @return boolean value indicating whether the objects are the same or not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeData data = (NodeData) o;
        return name.equals(data.name) &&
                font.equals(data.font) &&
                fillColor.equals(data.fillColor) &&
                borderColor.equals(data.borderColor) &&
                textColor.equals(data.textColor) &&
                style == data.style &&
                bounds.equals(data.bounds);
    }

    /**
     * Overriding the equals method requires us to also override the hashcode
     * method for the same purpose.
     * @return a boolean indicating whether the objects are the same according to
     * their hashcode or not.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, font, fillColor, borderColor, textColor, style, bounds);
    }
}
