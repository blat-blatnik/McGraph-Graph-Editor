package utils;

import java.awt.*;
import java.awt.geom.*;

/**
* @version 1.0
 *
 * This abstract class represents a diamond (a.k.a. rhombus) shape. It fully implements the Shape interface, meaning
 * it can be used for painting with Graphics/Graphics2D. Just like Rectangle2D, Point2D, and other shapes, there are
 * 2 default implementations of Diamond2D: Diamond2D.Double and Diamond2D.Float.
 *
 * @see Shape
 * @see PathIterator
 */
public abstract class Diamond2D implements Shape {

    /**
     * @return The x coordinate of the center of the Diamond2D.
     */
    public double getCenterX() {
        return getBounds2D().getCenterX();
    }

    /**
     * @return The y coordinate of the center of the Diamond2D.
     */
    public double getCenterY() {
        return getBounds2D().getCenterY();
    }

    /**
     * @return The width of the Diamond2D.
     */
    public double getWidth() {
        return getBounds2D().getWidth();
    }

    /**
     * @return The height of the Diamond2D.
     */
    public double getHeight() {
        return getBounds2D().getHeight();
    }

    /**
     * @return The left point of the Diamond2D.
     * @see Point2D
     */
    public Point2D getLeft() {
        return new Point2D.Double(getCenterX() - getWidth() / 2.0, getCenterY());
    }

    /**
     * @return The right point of the Diamond2D.
     * @see Point2D
     */
    public Point2D getRight() {
        return new Point2D.Double(getCenterX() + getWidth() / 2.0, getCenterY());
    }

    /**
     * @return The top point of the Diamond2D.
     * @see Point2D
     */
    public Point2D getTop() {
        return new Point2D.Double(getCenterX(), getCenterY() - getHeight() / 2.0);
    }

    /**
     * @return The bottom point of the Diamond2D.
     * @see Point2D
     */
    public Point2D getBottom() {
        return new Point2D.Double(getCenterX(), getCenterY() + getHeight() / 2.0);
    }

    /**
     * @return The line connecting the top and left points of the Diamond2D.
     * @see Line2D
     */
    public Line2D getTopLeft() {
        return new Line2D.Double(getLeft(), getTop());
    }

    /**
     * @return The line connecting the top and right points of the Diamond2D.
     * @see Line2D
     */
    public Line2D getTopRight() {
        return new Line2D.Double(getTop(), getRight());
    }

    /**
     * @return The line connecting the bottom and right points of the Diamond2D.
     * @see Line2D
     */
    public Line2D getBottomRight() {
        return new Line2D.Double(getBottom(), getRight());
    }

    /**
     * @return The line connecting the bottom and left points of the Diamond2D.
     * @see Line2D
     */
    public Line2D getBottomLeft() {
        return new Line2D.Double(getBottom(), getLeft());
    }

    /**
     * @return The smallest integer bounding Rectangle that fully encloses the Diamond2D.
     * @see Rectangle
     */
    @Override
    public Rectangle getBounds() {
        Rectangle2D bounds = getBounds2D();
        return new Rectangle(
                (int)Math.floor(bounds.getX()),
                (int)Math.floor(bounds.getY()),
                (int)Math.ceil(bounds.getWidth()),
                (int)Math.ceil(bounds.getHeight()));
    }

    /**
     * Moves and resizes this Diamond2D so that it exactly fits within the given Rectangle2D.
     *
     * @param frame The Rectangle2D in which to fit this Diamond into.
     * @see Rectangle2D
     */
    abstract void setFrame(Rectangle2D frame);

    /**
     * Moves and resizes this Diamond2D so that it exactly fits within a rectangle with the given dimensions.
     *
     * @param x The x coordinate of the upper-left corner of the new bounding frame.
     * @param y The y coordinate of the upper-left corner of the new bounding frame.
     * @param width The width of the new bounding frame.
     * @param height The height of the new bounding frame.
     */
    void setFrame(double x, double y, double width, double height) {
        setFrame(new Rectangle2D.Double(x, y, width, height));
    }

    /**
     * Moves and resizes this Diamond2D so that it exactly fits within a rectangle with the given dimensions.
     *
     * @param x The x coordinate of the upper-left corner of the new bounding frame.
     * @param y The y coordinate of the upper-left corner of the new bounding frame.
     * @param width The width of the new bounding frame.
     * @param height The height of the new bounding frame.
     */
    void setFrame(float x, float y, float width, float height) {
        setFrame(new Rectangle2D.Float(x, y, width, height));
    }

    /**
     * @param x The x coordinate of the point to test.
     * @param y The y coordinate of the point to test.
     * @return Whether the Rectangle2D encloses the given point.
     */
    @Override
    public boolean contains(double x, double y) {
        double dx = Math.abs(x - getCenterX());
        double dy = Math.abs(y - getCenterY());
        double sumDistance = dx / (0.5 * getWidth()) + dy / (0.5 * getHeight());
        return sumDistance <= 1;
    }

    /**
     * @param p The Point2D to test.
     * @return Whether the Rectangle2D encloses the given point.
     * @see Point2D
     */
    @Override
    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }

    /**
     * @param x The x coordinate of the top-left point of the rectangle to test.
     * @param y The y coordinate of the top-left point of the rectangle to test.
     * @param w The width of the rectangle to test.
     * @param h The height of the rectangle to test.
     * @return Whether any of the points defining the Diamond2D intersect with the given rectangle.
     */
    @Override
    public boolean intersects(double x, double y, double w, double h) {
        return intersects(new Rectangle2D.Double(x, y, w, h));
    }

    /**
     * @param r The Rectangle2D to test against.
     * @return Whether any of the points defining the Diamond2D intersect with the given Rectangle2D.
     * @see Rectangle2D
     */
    @Override
    public boolean intersects(Rectangle2D r) {
        return
                contains(r) ||
                r.intersectsLine(getTopLeft()) ||
                r.intersectsLine(getTopRight()) ||
                r.intersectsLine(getBottomRight()) ||
                r.intersectsLine(getBottomLeft());
    }

    /**
     * @param x The x coordinate of the top-left point of the rectangle to test.
     * @param y The y coordinate of the top-left point of the rectangle to test.
     * @param w The width of the rectangle to test.
     * @param h The height of the rectangle to test.
     * @return Whether the Diamond2D encloses the entire given rectangle.
     */
    @Override
    public boolean contains(double x, double y, double w, double h) {
        return
                contains(x, y) &&
                contains(x + w, y) &&
                contains(x, y + h) &&
                contains(x + w, y + h);
    }

    /**
     * @param r The Rectangle2D to test against.
     * @return Whether the Diamond2D encloses the entire given Rectangle2D.
     * @see Rectangle2D
     */
    @Override
    public boolean contains(Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /**
     * @param at The AffineTransform to apply to all points of the Diamond2D, or null if no transform is needed.
     * @return A PathIterator used to define the shape of this Diamond2D, and to draw it with Graphics2D.
     * @see PathIterator
     * @see AffineTransform
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return new PathIterator() {

            final Point2D[] points = new Point2D[]{ getLeft(), getTop(), getRight(), getBottom() };
            int currentIndex = 0;

            @Override
            public int getWindingRule() {
                return PathIterator.WIND_EVEN_ODD;
            }

            @Override
            public boolean isDone() {
                return currentIndex > points.length || currentIndex < 0;
            }

            @Override
            public void next() {
                if (currentIndex >= 0 && currentIndex <= points.length)
                    ++currentIndex;
            }

            @Override
            public int currentSegment(float[] coords) {
                if (isDone() || currentIndex == points.length)
                    return SEG_CLOSE;

                Point2D p = points[currentIndex];
                if (at != null)
                    p = at.transform(p, p);

                coords[0] = (float)p.getX();
                coords[1] = (float)p.getY();
                if (currentIndex == 0)
                    return SEG_MOVETO;
                else
                    return SEG_LINETO;
            }

            @Override
            public int currentSegment(double[] coords) {
                if (isDone() || currentIndex == points.length)
                    return SEG_CLOSE;

                Point2D p = points[currentIndex];
                if (at != null)
                    p = at.transform(p, p);

                coords[0] = p.getX();
                coords[1] = p.getY();
                if (currentIndex == 0)
                    return SEG_MOVETO;
                else
                    return SEG_LINETO;
            }
        };
    }

    /**
     * @param at The AffineTransform to apply to all points of the Diamond2D, or null if no transform is needed.
     * @param flatness The value of this parameter is ignored.
     * @return A PathIterator used to define the shape of this Diamond2D, and to draw it with Graphics2D.
     * @see PathIterator
     * @see AffineTransform
     */
    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return getPathIterator(at);
    }

    /**
     * An implementation of Diamond2D using IEEE double precision floating point to define the shape.
     */
    public static class Double extends Diamond2D {

        /**
         * The x coordinate of the center point of this Diamond2D.
         */
        public double centerX;

        /**
         * The y coordinate of the center point of this Diamond2D.
         */
        public double centerY;

        /**
         * The width of this Diamond2D.
         */
        public double width;

        /**
         * The height of this Diamond2D.
         */
        public double height;

        /**
         * Constructs a degenerate Diamond2D centered at (0, 0) and with a width and height of 0.
         */
        public Double() {
            this(0, 0, 0, 0);
        }

        /**
         * Constructs a Diamond2D that will fit inside a given bounding Rectangle2D. The width and height of the
         * new Diamond2D will be equal to the width and height of the given Rectangle, and The new Diamond2D will be
         * centered on the center point of the given Rectangle.
         *
         * @param bounds The bounding Rectangle2D in which to fit this Diamond2D into.
         * @see Rectangle2D
         */
        public Double(Rectangle2D bounds) {
            this(bounds.getCenterX(), bounds.getCenterY(), bounds.getWidth(), bounds.getHeight());
        }

        /**
         * Construct a new Diamond2D.
         *
         * @param centerX The x coordinate of the center point of this Diamond2D.
         * @param centerY The y coordinate of the center point of this Diamond2D.
         * @param width The width of this Diamond2D.
         * @param height The height of this Diamond2D.
         */
        public Double(double centerX, double centerY, double width, double height) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
        }

        /**
         * @return The smallest bounding Rectangle2D that can enclose all the points of this Diamond2D.
         */
        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Double(centerX - width / 2, centerY - height / 2, width, height);
        }

        /**
         * Resizes this Diamond2D so that it exactly fits within the given Rectangle2D.
         *
         * @param frame The Rectangle2D in which to fit this Diamond into.
         * @see Rectangle2D
         */
        @Override
        public void setFrame(Rectangle2D frame) {
            this.centerX = frame.getCenterX();
            this.centerY = frame.getCenterY();
            this.width = frame.getWidth();
            this.height = frame.getHeight();
        }
    }

    /**
     * An implementation of Diamond2D using IEEE single precision floating point to define the shape.
     */
    public static class Float extends Diamond2D {

        /**
         * The x coordinate of the center point of this Diamond2D.
         */
        public float centerX;

        /**
         * The y coordinate of the center point of this Diamond2D.
         */
        public float centerY;

        /**
         * The width of this Diamond2D.
         */
        public float width;

        /**
         * The height of this Diamond2D.
         */
        public float height;

        /**
         * Constructs a degenerate Diamond2D centered at (0, 0) and with a width and height of 0.
         */
        public Float() {
            this(0, 0, 0, 0);
        }

        /**
         * Constructs a Diamond2D that will fit inside a given bounding Rectangle2D. The width and height of the
         * new Diamond2D will be equal to the width and height of the given Rectangle, and The new Diamond2D will be
         * centered on the center point of the given Rectangle.
         *
         * @param bounds The bounding Rectangle2D in which to fit this Diamond2D into.
         * @see Rectangle2D
         */
        public Float(Rectangle2D bounds) {
            this((float)bounds.getCenterX(), (float)bounds.getCenterY(), (float)bounds.getWidth(), (float)bounds.getHeight());
        }

        /**
         * Construct a new Diamond2D.
         *
         * @param centerX The x coordinate of the center point of this Diamond2D.
         * @param centerY The y coordinate of the center point of this Diamond2D.
         * @param width The width of this Diamond2D.
         * @param height The height of this Diamond2D.
         */
        public Float(float centerX, float centerY, float width, float height) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
        }

        /**
         * @return The smallest bounding Rectangle2D that can enclose all the points of this Diamond2D.
         */
        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Float(centerX - width / 2, centerY - height / 2, width, height);
        }

        /**
         * Resizes this Diamond2D so that it exactly fits within the given Rectangle2D.
         *
         * @param frame The Rectangle2D in which to fit this Diamond into.
         * @see Rectangle2D
         */
        @Override
        public void setFrame(Rectangle2D frame) {
            this.centerX = (float)frame.getCenterX();
            this.centerY = (float)frame.getCenterY();
            this.width = (float)frame.getWidth();
            this.height = (float)frame.getHeight();
        }
    }

}