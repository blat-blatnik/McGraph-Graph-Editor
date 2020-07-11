package model;

/**
 * @author Boris
 * @version 1.0
 *
 * This enum encodes which visual style an Edge has.
 *
 * @see Edge
 * @see java.awt.geom.Line2D
 * @see java.awt.geom.QuadCurve2D
 * @see java.awt.geom.CubicCurve2D
 * @see java.awt.geom.Path2D
 */
public enum EdgeStyle {
    ELBOW_JOINT,
    QUADRATIC_BEZIER,
    CUBIC_BEZIER,
}