package model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Objects;

/**
 * @author Boris
 * @author Jana
 * @version 2.0
 *
 * Data class holds all data of the edge. The edge will be able to have multiple
 * versions of this data, in order to have some only visual data and some actual
 * data. It can also be used in cloning an edge.
 *
 * @see Edge
 * @see EdgeDirection
 * @see EdgeStyle
 * @see Color
 * @see Point2D
 */
public class EdgeData {

    private double weight;
    private EdgeDirection direction;
    private EdgeStyle style;
    private Color color;
    private Point2D weightPoint;

    /**
     * Constructs a data object without initial values. They can be assigned afterwards.
     */
    public EdgeData(){}

    /**
     * Copy constructor: this can be used to clone data.
     * @param other the other data this data will be cloned from
     */
    public EdgeData(EdgeData other){
        direction = other.direction;
        style = other.style;
        color = other.color;
        weight = other.weight;
        weightPoint = new Point2D.Double(other.weightPoint.getX(), other.weightPoint.getY());
    }

    /**
     * Gets the weight of the data.
     * @return weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the data.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Gets the direction of the edge data.
     * @return direction
     */
    public EdgeDirection getDirection() {
        return direction;
    }

    /**
     * Sets the direction of the data.
     */
    public void setDirection(EdgeDirection direction) {
        this.direction = direction;
    }

    /**
     * Gets the style of the edge data.
     * @return style
     */
    public EdgeStyle getStyle() {
        return style;
    }

    /**
     * Gets the style of the edge data.
     */
    public void setStyle(EdgeStyle style) {
        this.style = style;
    }

    /**
     * Gets the color of the edge data.
     * @return color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of the edge data.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Gets the weight-point of the edge data.
     * @return weight-point
     */
    public Point2D getWeightPoint() {
        return weightPoint;
    }

    /**
     * Sets the weight-point of the edge data.
     */
    public void setWeightPoint(Point2D weightPoint) {
        this.weightPoint = weightPoint;
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
        EdgeData data = (EdgeData) o;
        return Double.compare(data.weight, weight) == 0 &&
                direction == data.direction &&
                style == data.style &&
                color.equals(data.color) &&
                weightPoint.equals(data.weightPoint);
    }

    /**
     * Overriding the equals method requires us to also override the hashcode
     * method for the same purpose.
     * @return a boolean indicating whether the objects are the same according to
     * their hashcode or not.
     */
    @Override
    public int hashCode() {
        return Objects.hash(direction, style, color, weight, weightPoint);
    }
}
