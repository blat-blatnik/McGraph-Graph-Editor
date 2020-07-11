package model;

/**
 * @author Boris
 * @version 1.2
 *
 * This enum encodes which visual shape a Node has.
 *
 * @see Node
 * @see java.awt.geom.Rectangle2D
 * @see java.awt.geom.RoundRectangle2D
 * @see java.awt.geom.Ellipse2D
 * @see utils.Diamond2D
 */
public enum NodeStyle {
    RECTANGLE,
    ROUNDED_RECTANGLE,
    ELLIPSE,
    DIAMOND
}