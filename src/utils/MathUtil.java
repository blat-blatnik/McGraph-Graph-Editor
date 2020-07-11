package utils;

import java.awt.Color;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 999.0
*
 * This class contains a bunch of useful mathematical operations on scalars, Points, Lines, Rectangles, and other Shapes
 * as well as Colors.
 *
 * @see Rectangle2D
 * @see Line2D
 * @see Point2D
 * @see Color
 * @see QuadCurve2D
 * @see CubicCurve2D
 * @see Path2D
 */
public final class MathUtil {

    /**
     * @param rect1 The Rectangle2D from which the Line2D should start.
     * @param rect2 The Rectangle2D from which the Line2D should end.
     * @return A line directed from the center of one Rectangle to the other, starting from an edge on rect1 and ending at an edge on rect2.
     */
    public static Line2D lineBetween(Rectangle2D rect1, Rectangle2D rect2) {
        Line2D line = new Line2D.Double(
                rect1.getCenterX(),
                rect1.getCenterY(),
                rect2.getCenterX(),
                rect2.getCenterY());
        line = cutByRectangle(line, rect1);
        line = cutByRectangle(line, rect2);
        return line;
    }

    /**
     * @param p1 The Point2D at which the curve should start.
     * @param p2 The Point2D that the curve should pass through between p1 and p3.
     * @param p3 The Point2D which the curve should end at.
     * @return A quadratic Bezier curve starting at p1, passing through p2, and ending at p3.
     */
    public static QuadCurve2D quadraticCurveThrough(Point2D p1, Point2D p2, Point2D p3) {

        final double t = 0.5;

        double w1 = (1 - t) * (1 - t);
        double w3 = t * t;
        Point2D num = sub(sub(p2, mul(w1, p1)), mul(w3, p3));
        Point2D controlPoint = mul(1 / (2 * (1 - t) * t), num);
        QuadCurve2D curve = new QuadCurve2D.Double();
        curve.setCurve(p1, controlPoint, p3);
        return curve;

    }

    /**
     * @param p1 The Point2D which the spline should start from.
     * @param p2 The Point2D which the spline should pass through in between p1 and p3.
     * @param p3 The Point2D which the spline should pass through in between p2 and p4.
     * @param p4 The Point2D which the spline should pass through in between p3 and p5.
     * @param p5 The Point2D which the spline should end at.
     * @return A Path2D containing 4 continuous cubic Bezier curves that pass through the given points in order.
     */
    public static Path2D cubicSplineThrough(Point2D p1, Point2D p2, Point2D p3, Point2D p4, Point2D p5) {

        //NOTE(Boris): Adapted from https://www.math.ucla.edu/~baker/149.1.02w/handouts/dd_splines.pdf

        float[][] s = {
                { (float)p1.getX(), (float)p1.getY() },
                { (float)p2.getX(), (float)p2.getY() },
                { (float)p3.getX(), (float)p3.getY() },
                { (float)p4.getX(), (float)p4.getY() },
                { (float)p5.getX(), (float)p5.getY() }
        };

        float r0x = 6 * s[1][0] - s[0][0];
        float r0y = 6 * s[1][1] - s[0][1];
        float r1x = 6 * s[2][0];
        float r1y = 6 * s[2][1];
        float r2x = 6 * s[3][0] - s[4][0];
        float r2y = 6 * s[3][1] - s[4][1];

        float[][] controlMatrix3x2 = {
                { r0x, r0y },
                { r1x, r1y },
                { r2x, r2y }
        };

        final float[][] INVERSE_BEZIER = {
                { 15 / 56.0f, -4 / 56.0f,  1 / 56.0f },
                { -4 / 56.0f, 16 / 56.0f, -4 / 56.0f },
                {  1 / 56.0f, -4 / 56.0f, 15 / 56.0f }
        };

        float[][] controlPointData3x2 = multiplyMatrix3x3WithMatrix3x2(INVERSE_BEZIER, controlMatrix3x2);

        float[][] b = new float[5][2];
        b[0][0] = s[0][0];
        b[0][1] = s[0][1];
        b[1][0] = controlPointData3x2[0][0];
        b[1][1] = controlPointData3x2[0][1];
        b[2][0] = controlPointData3x2[1][0];
        b[2][1] = controlPointData3x2[1][1];
        b[3][0] = controlPointData3x2[2][0];
        b[3][1] = controlPointData3x2[2][1];
        b[4][0] = s[4][0];
        b[4][1] = s[4][1];

        Path2D.Float spline = new Path2D.Float(Path2D.WIND_NON_ZERO, 4);
        spline.moveTo(s[0][0], s[0][1]);

        for (int i = 0; i < 4; ++i) {
            float control1X = (2.0f / 3.0f) * b[i][0] + (1.0f / 3.0f) * b[i + 1][0];
            float control1y = (2.0f / 3.0f) * b[i][1] + (1.0f / 3.0f) * b[i + 1][1];
            float control2x = (1.0f / 3.0f) * b[i][0] + (2.0f / 3.0f) * b[i + 1][0];
            float control2y = (1.0f / 3.0f) * b[i][1] + (2.0f / 3.0f) * b[i + 1][1];
            float control3x = s[i + 1][0];
            float control3y = s[i + 1][1];
            spline.curveTo(control1X, control1y, control2x, control2y, control3x, control3y);
        }

        return spline;
    }

    /**
     * @param rect1 The Rectangle2D from which the elbow joint should start.
     * @param point2 The Point2D through which the elbow joint should pass in between rect1 and rect2.
     * @param rect3 The Rectangle2D at which the elbow joint should end.
     * @return A Path2D containing 4 straight lines that form an elbow joint that pass through the given geometry.
     */
    public static Path2D elbowJointThrough(Rectangle2D rect1, Point2D point2, Rectangle2D rect3) {

        Point2D point1 = new Point2D.Double();
        Point2D point3 = new Point2D.Double();
        int out1 = centerOfEdgeClosestToPoint(rect1, point2, point1);
        int out3 = centerOfEdgeClosestToPoint(rect3, point2, point3);

        double x1 = point1.getX();
        double y1 = point1.getY();
        double x2 = point2.getX();
        double y2 = point2.getY();
        double x3 = point3.getX();
        double y3 = point3.getY();

        Path2D path = new Path2D.Float();

        path.moveTo(x1, y1);

        if (out1 == Rectangle2D.OUT_LEFT || out1 == Rectangle2D.OUT_RIGHT)
            path.lineTo(x2, y1);
        else
            path.lineTo(x1, y2);

        path.lineTo(x2, y2);

        if (out3 == Rectangle2D.OUT_TOP || out3 == Rectangle2D.OUT_BOTTOM)
            path.lineTo(x3, y2);
        else
            path.lineTo(x2, y3);

        path.lineTo(x3, y3);

        return path;
    }

    /**
     * @param path The Path2D whose segment points to determine.
     * @return A List containing sequential pairs of (start, end) points for each segment of the given path.
     */
    public static List<Point2D> pathSegmentPoints(Path2D path) {

        PathIterator iterator = path.getPathIterator(null);
        double[] coords = new double[6];

        double lastX = 0;
        double lastY = 0;
        double moveX = 0;
        double moveY = 0;

        java.util.List<Point2D> points = new ArrayList<>();

        while (!iterator.isDone()) {
            int segType = iterator.currentSegment(coords);

            double x1 = coords[0];
            double y1 = coords[1];
            double x2 = coords[2];
            double y2 = coords[3];
            double x3 = coords[4];
            double y3 = coords[5];

            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    moveX = x1;
                    moveY = y1;
                    lastX = x1;
                    lastY = y1;
                    break;
                case PathIterator.SEG_CLOSE:
                    x1 = moveX;
                    y1 = moveY;
                    // fallthrough (no break)
                case PathIterator.SEG_LINETO:
                    points.add(new Point2D.Double(lastX, lastY));
                    points.add(new Point2D.Double(x1, y1));
                    lastX = x1;
                    lastY = y1;
                    break;
                case PathIterator.SEG_QUADTO:
                    points.add(new Point2D.Double(lastX, lastY));
                    points.add(new Point2D.Double(x2, y2));
                    lastX = x2;
                    lastY = y2;
                    break;
                case PathIterator.SEG_CUBICTO:
                    points.add(new Point2D.Double(lastX, lastY));
                    points.add(new Point2D.Double(x3, y3));
                    lastX = x3;
                    lastY = y3;
                    break;
            }

            iterator.next();
        }

        return points;

    }

    /**
     * @param path The Path2D whose segment start and end point angles to determine.
     * @return A List containing sequential pairs of (start, end) point angles for each segment of the given path.
     */
    public static List<Double> pathSegmentAngles(Path2D path) {

        PathIterator iterator = path.getPathIterator(null);
        double[] coords = new double[6];

        double lastX = 0;
        double lastY = 0;
        double moveX = 0;
        double moveY = 0;

        java.util.List<Double> angles = new ArrayList<>();

        while (!iterator.isDone()) {
            int segType = iterator.currentSegment(coords);

            double x1 = coords[0];
            double y1 = coords[1];
            double x2 = coords[2];
            double y2 = coords[3];
            double x3 = coords[4];
            double y3 = coords[5];

            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    moveX = x1;
                    moveY = y1;
                    lastX = x1;
                    lastY = y1;
                    break;
                case PathIterator.SEG_CLOSE:
                    x1 = moveX;
                    y1 = moveY;
                    // fallthrough (no break)
                case PathIterator.SEG_LINETO:
                    double angle1 = Math.atan2(lastY - y1, lastX - x1);
                    double angle2 = angle1 + Math.PI;
                    angles.add(angle1);
                    angles.add(angle2);
                    lastX = x1;
                    lastY = y1;
                    break;
                case PathIterator.SEG_QUADTO:
                    angle1 = Math.atan2(lastY - y1, lastX - x1);
                    angle2 = Math.atan2(y2 - y1, x2 - x1);
                    angles.add(angle1);
                    angles.add(angle2);
                    lastX = x2;
                    lastY = y2;
                    break;
                case PathIterator.SEG_CUBICTO:
                    angle1 = Math.atan2(lastY - y1, lastX - x1);
                    angle2 = Math.atan2(y3 - y2, x3 - x2);
                    angles.add(angle1);
                    angles.add(angle2);
                    lastX = x3;
                    lastY = y3;
                    break;
            }

            iterator.next();
        }

        return angles;

    }

    /**
     * @param rect The Rectangle2D whose center of edge to determine.
     * @param point The Point2D whose closest center of edge in the Rectangle to determine.
     * @param outPoint The Point2D where the center of edge of the Rectangle is stored.
     * @return Rectangle2D.OUT_LEFT if the left edge center was stored in outPoint, Rectangle2D.OUT_RIGHT if the right edge center was stored in outPoint, etc.
     */
    public static int centerOfEdgeClosestToPoint(Rectangle2D rect, Point2D point, Point2D outPoint) {

        Point2D topLeft = topLeft(rect);
        Point2D topRight = topRight(rect);
        Point2D bottomLeft = bottomLeft(rect);
        Point2D bottomRight = bottomRight(rect);

        double x = point.getX();
        double y = point.getY();
        double leftX = 0.5 * (topLeft.getX() + bottomLeft.getX());
        double leftY = 0.5 * (topLeft.getY() + bottomLeft.getY());
        double rightX = 0.5 * (bottomRight.getX() + topRight.getX());
        double rightY = 0.5 * (bottomRight.getY() + topRight.getY());
        double topX = 0.5 * (topLeft.getX() + topRight.getX());
        double topY = 0.5 * (topLeft.getY() + topRight.getY());
        double bottomX = 0.5 * (bottomLeft.getX() + bottomRight.getX());
        double bottomY = 0.5 * (bottomLeft.getY() + bottomRight.getY());

        double leftDist = Point2D.distanceSq(x, y, leftX, leftY);
        double rightDist = Point2D.distanceSq(x, y, rightX, rightY);
        double topDist = Point2D.distanceSq(x, y, topX, topY);
        double bottomDist = Point2D.distanceSq(x, y, bottomX, bottomY);

        int out = rect.outcode(point);
        double minDist = Double.POSITIVE_INFINITY;
        int minOutCode = 0;

        if (leftDist < minDist && (out & Rectangle2D.OUT_LEFT) != 0) {
            minDist = leftDist;
            outPoint.setLocation(leftX, leftY);
            minOutCode = Rectangle2D.OUT_LEFT;
        }

        if (rightDist < minDist && (out & Rectangle2D.OUT_RIGHT) != 0) {
            minDist = rightDist;
            outPoint.setLocation(rightX, rightY);
            minOutCode = Rectangle2D.OUT_RIGHT;
        }

        if (topDist < minDist && (out & Rectangle2D.OUT_TOP) != 0) {
            minDist = topDist;
            outPoint.setLocation(topX, topY);
            minOutCode = Rectangle2D.OUT_TOP;
        }

        if (bottomDist < minDist && (out & Rectangle2D.OUT_BOTTOM) != 0) {
            outPoint.setLocation(bottomX, bottomY);
            minOutCode = Rectangle2D.OUT_BOTTOM;
        }

        return minOutCode;
    }

    /**
     * @param line The Line2D whose center to determine.
     * @return The Point2D that lies on the center of the given line.
     */
    public static Point2D centerPoint(Line2D line) {
        double x = 0.5 * (line.getX1() + line.getX2());
        double y = 0.5 * (line.getY1() + line.getY2());
        return new Point2D.Double(x, y);
    }

    /**
     * @param line1 The 1st Line2D to intersect.
     * @param line2 The 2nd Line2D to intersect.
     * @return The Point of intersection between the two lines, or null if the lines do not intersect. The results are undefined if the lines are co-linear.
     */
    public static Point2D intersection(Line2D line1, Line2D line2) {

        //NOTE(Boris): Adapted from https://stackoverflow.com/a/565282.
        // I did not implement the parts that check for collinearity because this is a case
        // we should *hopefully* never encounter - it makes the code a lot simpler and easier to digest.

        Point2D p1 = line1.getP1();
        Point2D p2 = line1.getP2();
        Point2D q1 = line2.getP1();
        Point2D q2 = line2.getP2();

        Point2D p1p2 = sub(p2, p1);
        Point2D q1q2 = sub(q2, q1);
        Point2D p1q1 = sub(q1, p1);

        // Get the area of the parallelogram created between the direction vectors of the 2 lines.
        double area = cross(p1p2, q1q2);

        // If the area is 0, the lines are perfectly parallel and there is obviously no intersection.
        if (area == 0)
            return null;

        double t = cross(p1q1, q1q2) / area;
        double u = cross(p1q1, p1p2) / area;

        // If we can find a t or a u in range, then we have a definite intersection point.
        if ((0 <= t && t <= 1) && (0 <= u && u <= 1))
            return add(p1, mul(t, p1p2));

        // Otherwise the line segments do not intersect.
        return null;
    }

    /**
     * @param curve The QuadCurve2D to intersect.
     * @param rect The Rectangle2D to intersect.
     * @param threshold The curve will be subdivided until it is shorter than this threshold and then treated as a straight line.
     * @return Whether the given rectangle intersects the given quadratic bezier curve.
     */
    public static boolean intersects(QuadCurve2D curve, Rectangle2D rect, double threshold) {
        Line2D line = new Line2D.Double(curve.getP1(), curve.getP2());
        if (length(line) < threshold)
            return line.intersects(rect);
        else if (curve.intersects(rect)) {
            QuadCurve2D left = new QuadCurve2D.Double();
            QuadCurve2D right = new QuadCurve2D.Double();
            curve.subdivide(left, right);
            return intersects(left, rect, threshold) || intersects(right, rect, threshold);
        } else
            return false;
    }

    /**
     * @param curve The CubicCurve2D to intersect.
     * @param rect The Rectangle2D to intersect.
     * @param threshold The curve will be subdivided until it is shorter than this threshold and then treated as a straight line.
     * @return Whether the given rectangle intersects the given cubic bezier curve.
     */
    public static boolean intersects(CubicCurve2D curve, Rectangle2D rect, double threshold) {
        Line2D line = new Line2D.Double(curve.getP1(), curve.getP2());
        if (length(line) < threshold)
            return line.intersects(rect);
        else if (curve.intersects(rect)) {
            CubicCurve2D left = new CubicCurve2D.Double();
            CubicCurve2D right = new CubicCurve2D.Double();
            curve.subdivide(left, right);
            return intersects(left, rect, threshold) || intersects(right, rect, threshold);
        } else
            return false;
    }

    /**
     * @param path The Path2D to check for intersections.
     * @param rect The Rectangle2D to check for intersections.
     * @param threshold Bezier curves will be subdivided until this threshold length - and then treated as straight lines.
     * @return Whether the given rectangle intersects any of the segments of the given path.
     */
    public static boolean intersects(Path2D path, Rectangle2D rect, double threshold) {
        PathIterator iterator = path.getPathIterator(null);
        double[] coords = new double[6];

        double lastX = 0;
        double lastY = 0;
        double moveX = 0;
        double moveY = 0;

        while (!iterator.isDone()) {
            int segType = iterator.currentSegment(coords);

            double x1 = coords[0];
            double y1 = coords[1];
            double x2 = coords[2];
            double y2 = coords[3];
            double x3 = coords[4];
            double y3 = coords[5];

            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    moveX = x1;
                    moveY = y1;
                    lastX = x1;
                    lastY = y1;
                    break;
                case PathIterator.SEG_CLOSE:
                    x1 = moveX;
                    y1 = moveY;
                    // fallthrough (no break)
                case PathIterator.SEG_LINETO:
                    Line2D line = new Line2D.Double(lastX, lastY, x1, y1);
                    if (line.intersects(rect))
                        return true;
                    lastX = x1;
                    lastY = y1;
                    break;
                case PathIterator.SEG_QUADTO:
                    QuadCurve2D quadBezier = new QuadCurve2D.Double(lastX, lastY, x1, y1, x2, y2);
                    if (intersects(quadBezier, rect, threshold))
                        return true;
                    lastX = x2;
                    lastY = y2;
                    break;
                case PathIterator.SEG_CUBICTO:
                    CubicCurve2D cubicBezier = new CubicCurve2D.Double(lastX, lastY, x1, y1, x2, y2, x3, y3);
                    if (intersects(cubicBezier, rect, threshold))
                        return true;
                    lastX = x3;
                    lastY = y3;
                    break;
            }

            iterator.next();
        }

        return false;
    }

    /**
     * @param curve The QuadCurve2D to intersect.
     * @param rect The Rectangle2D to intersect.
     * @param threshold The curve will be subdivided until it is shorter than this threshold and then treated as a straight line.
     * @return Whether the given rectangle completely encloses the given quadratic bezier curve.
     */
    public static boolean contains(Rectangle2D rect, QuadCurve2D curve, double threshold) {
        if (rect.contains(curve.getBounds2D()))
            return true;

        Point2D p1 = curve.getP1();
        Point2D p2 = curve.getP2();

        if (rect.contains(p1) && rect.contains(p2)) {
            Line2D line = new Line2D.Double(p1, p2);
            if (length(line) < threshold)
                return true;
            else {
                QuadCurve2D left = new QuadCurve2D.Double();
                QuadCurve2D right = new QuadCurve2D.Double();
                curve.subdivide(left, right);
                return contains(rect, left, threshold) && contains(rect, right, threshold);
            }
        } else
            return false;
    }

    /**
     * @param curve The CubicCurve2D to intersect.
     * @param rect The Rectangle2D to intersect.
     * @param threshold The curve will be subdivided until it is shorter than this threshold and then treated as a straight line.
     * @return Whether the given rectangle completely encloses the given cubic bezier curve.
     */
    public static boolean contains(Rectangle2D rect, CubicCurve2D curve, double threshold) {
        if (rect.contains(curve.getBounds2D()))
            return true;

        Point2D p1 = curve.getP1();
        Point2D p2 = curve.getP2();

        if (rect.contains(p1) && rect.contains(p2)) {
            Line2D line = new Line2D.Double(p1, p2);
            if (length(line) < threshold)
                return true;
            else {
                CubicCurve2D left = new CubicCurve2D.Double();
                CubicCurve2D right = new CubicCurve2D.Double();
                curve.subdivide(left, right);
                return contains(rect, left, threshold) && contains(rect, right, threshold);
            }
        } else
            return false;
    }

    /**
     * @param path The Path2D to check for intersections.
     * @param rect The Rectangle2D to check for intersections.
     * @param threshold Bezier curves will be subdivided until this threshold length - and then treated as straight lines.
     * @return Whether the given rectangle completely encloses all of the segments of the given path.
     */
    public static boolean contains(Rectangle2D rect, Path2D path, double threshold) {
        PathIterator iterator = path.getPathIterator(null);
        double[] coords = new double[6];

        double lastX = 0;
        double lastY = 0;
        double moveX = 0;
        double moveY = 0;

        while (!iterator.isDone()) {
            int segType = iterator.currentSegment(coords);

            double x1 = coords[0];
            double y1 = coords[1];
            double x2 = coords[2];
            double y2 = coords[3];
            double x3 = coords[4];
            double y3 = coords[5];

            switch (segType) {
                case PathIterator.SEG_MOVETO:
                    moveX = x1;
                    moveY = y1;
                    lastX = x1;
                    lastY = y1;
                    break;
                case PathIterator.SEG_CLOSE:
                    x1 = moveX;
                    y1 = moveY;
                    // fallthrough (no break)
                case PathIterator.SEG_LINETO:
                    Line2D line = new Line2D.Double(lastX, lastY, x1, y1);
                    if (!rect.contains(line.getP1()) || !rect.contains(line.getP2()))
                        return false;
                    lastX = x1;
                    lastY = y1;
                    break;
                case PathIterator.SEG_QUADTO:
                    QuadCurve2D quadBezier = new QuadCurve2D.Double(lastX, lastY, x1, y1, x2, y2);
                    if (!contains(rect, quadBezier, threshold))
                        return false;
                    lastX = x2;
                    lastY = y2;
                    break;
                case PathIterator.SEG_CUBICTO:
                    CubicCurve2D cubicBezier = new CubicCurve2D.Double(lastX, lastY, x1, y1, x2, y2, x3, y3);
                    if (!contains(rect, cubicBezier, threshold))
                        return false;
                    lastX = x3;
                    lastY = y3;
                    break;
            }

            iterator.next();
        }

        return true;
    }

    /**
     * @param line The Line2D whose *end points* to cut.
     * @param rect The Rectangle2D by which to cut out the given line.
     * @return The Line resulting from cutting the given line's end points by the given rectangle. If the line is entirely contained in the rectangle, the degenerate line (0,0) -> (0,0) is returned.
     */
    public static Line2D cutByRectangle(Line2D line, Rectangle2D rect) {
        Point2D p1 = line.getP1();
        Point2D p2 = line.getP2();
        boolean containsP1 = rect.contains(p1);
        boolean containsP2 = rect.contains(p2);

        if (containsP1 && containsP2)
            return new Line2D.Double(0, 0, 0, 0);
        else if (!containsP1 && !containsP2)
            return (Line2D)line.clone();

        //NOTE(Boris): Nothing too smart here, we just intersect the point that was contained in the rectangle with
        // all edges of that rectangle in sequence.

        Line2D leftEdge   = leftEdge(rect);
        Line2D rightEdge  = rightEdge(rect);
        Line2D topEdge    = topEdge(rect);
        Line2D bottomEdge = bottomEdge(rect);

        if (containsP1) {

            Point2D intersection;
            intersection = intersection(leftEdge, new Line2D.Double(p1, p2));
            if (intersection != null)
                p1 = intersection;
            intersection = intersection(rightEdge, new Line2D.Double(p1, p2));
            if (intersection != null)
                p1 = intersection;
            intersection = intersection(topEdge, new Line2D.Double(p1, p2));
            if (intersection != null)
                p1 = intersection;
            intersection = intersection(bottomEdge, new Line2D.Double(p1, p2));
            if (intersection != null)
                p1 = intersection;

        } else { //NOTE(Boris): containsP2

            Point2D intersection;
            intersection = intersection(leftEdge, new Line2D.Double(p1, p2));
            if (intersection != null)
                p2 = intersection;
            intersection = intersection(rightEdge, new Line2D.Double(p1, p2));
            if (intersection != null)
                p2 = intersection;
            intersection = intersection(topEdge, new Line2D.Double(p1, p2));
            if (intersection != null)
                p2 = intersection;
            intersection = intersection(bottomEdge, new Line2D.Double(p1, p2));
            if (intersection != null)
                p2 = intersection;
        }

        return new Line2D.Double(p1, p2);
    }

    /**
     * @param rect The Rectangle2D to grow (or shrink).
     * @param deltaWidth The amount by which to grow the width of the rectangle.
     * @param deltaHeight The amount by which to grow the height of the rectangle.
     * @return A rectangle that is centered on the same point as the given rectangle but with grown/shrunken dimensions. Note that shrinking a rectangle to negative width/height will result in a degenerate rectangle with a width/height of *0*.
     */
    public static Rectangle2D growRectangle(Rectangle2D rect, double deltaWidth, double deltaHeight) {
        Rectangle2D.Double grown = new Rectangle2D.Double();
        grown.x = rect.getX() - deltaWidth / 2;
        grown.y = rect.getY() - deltaHeight / 2;
        grown.width = Math.max(rect.getWidth() + deltaWidth, 0);
        grown.height = Math.max(rect.getHeight() + deltaHeight, 0);
        return grown;
    }

    /**
     * @param center The Point2D on which the rectangle should be centered.
     * @param width The width of the resulting rectangle.
     * @param height The height of the resulting rectangle.
     * @return A rectangle that is centered on the given point, and with the given width and height.
     */
    public static Rectangle2D centeredRectangle(Point2D center, double width, double height) {
        return new Rectangle2D.Double(center.getX() - width / 2, center.getY() - height / 2, width, height);
    }

    /**
     * @param centerX The x coordinate on which the rectangles should be centered.
     * @param centerY The y coordinate on which the rectangles should be centered.
     * @param width The width of the resulting rectangle.
     * @param height The height The height of the resulting rectangle.
     * @return A rectangle that is centered on the given point, and with the given width and height.
     */
    public static Rectangle2D centeredRectangle(double centerX, double centerY, double width, double height) {
        return centeredRectangle(new Point2D.Double(centerX, centerY), width, height);
    }

    /**
     * @param rect The Rectangle2D to turn into a rectangle square.
     * @return A Rectangle2D with square dimensions, centered on the given rectangle and having a side length of max(rect.width, rect.height).
     */
    public static Rectangle2D makeSquare(Rectangle2D rect) {
        double dimensions = Math.max(rect.getWidth(), rect.getHeight());
        return centeredRectangle(rect.getCenterX(), rect.getCenterY(), dimensions, dimensions);
    }

    /**
     * @param line The Line2D whose length to determine.
     * @return The length of the given line.
     */
    public static double length(Line2D line) {
        return distanceBetween(line.getP1(), line.getP2());
    }

    /**
     * @param p1 The 1st Point2D.
     * @param p2 The 2nd Point2D.
     * @return The distance between p1 and p2.
     */
    public static double distanceBetween(Point2D p1, Point2D p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * @param point The point.
     * @param line The line.
     * @return The shortest distance between the given point and the given line.
     */
    public static double distanceBetween(Point2D point, Line2D line) {
        return Line2D.ptSegDist(line.getX1(), line.getY1(), line.getX2(), line.getY2(), point.getX(), point.getY());
    }

    /**
     * @param point The Point2D to transfer.
     * @param oldLine The Line2D from whose coordinate space to transfer the point from.
     * @param newLine The Line2D whose coordinate space the point will be transferred to.
     * @return A Point2D that results from transferring the given point between the given lines.
     */
    public static Point2D transferPointBetweenLines(Point2D point, Line2D oldLine, Line2D newLine) {

        double dx1 = oldLine.getX2() - oldLine.getX1();
        double dy1 = oldLine.getY2() - oldLine.getY1();
        double dx2 = newLine.getX2() - newLine.getX1();
        double dy2 = newLine.getY2() - newLine.getY1();
        double length1 = length(oldLine);
        double length2 = length(newLine);
        double angle1 = Math.atan2(dy1, dx1);
        double angle2 = Math.atan2(dy2, dx2);

        double theta = angle2 - angle1;
        double s = Math.sin(theta);
        double c = Math.cos(theta);

        Point2D p = mul(1.0 / length1, sub(point, oldLine.getP1()));
        p.setLocation(
                c * p.getX() - s * p.getY(),
                s * p.getX() + c * p.getY());

        return add(mul(length2, p), newLine.getP1());
    }

    /**
     * @param rect The Rectangle2D whose top-left corner point to get.
     * @return The top-left corner point of the given rectangle.
     */
    public static Point2D topLeft(Rectangle2D rect) {
        return new Point2D.Double(rect.getX(), rect.getY());
    }

    /**
     * @param rect The Rectangle2D whose top-right corner point to get.
     * @return The top-right corner point of the given rectangle.
     */
    public static Point2D topRight(Rectangle2D rect) {
        return new Point2D.Double(rect.getX() + rect.getWidth(), rect.getY());
    }

    /**
     * @param rect The Rectangle2D whose bottom-left corner point to get.
     * @return The bottom-left corner point of the given rectangle.
     */
    public static Point2D bottomLeft(Rectangle2D rect) {
        return new Point2D.Double(rect.getX(), rect.getY() + rect.getHeight());
    }

    /**
     * @param rect The Rectangle2D whose bottom-right corner point to get.
     * @return The bottom-right corner point of the given rectangle.
     */
    public static Point2D bottomRight(Rectangle2D rect) {
        return new Point2D.Double(rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
    }

    /**
     * @param rect The Rectangle2D whose left edge line to get.
     * @return A Line2D tracing the left edge of the given rectangle.
     */
    public static Line2D leftEdge(Rectangle2D rect) {
        return new Line2D.Double(topLeft(rect), bottomLeft(rect));
    }

    /**
     * @param rect The Rectangle2D whose right edge line to get.
     * @return A Line2D tracing the right edge of the given rectangle.
     */
    public static Line2D rightEdge(Rectangle2D rect) {
        return new Line2D.Double(topRight(rect), bottomRight(rect));
    }

    /**
     * @param rect The Rectangle2D whose top edge line to get.
     * @return A Line2D tracing the top edge of the given rectangle.
     */
    public static Line2D topEdge(Rectangle2D rect) {
        return new Line2D.Double(topLeft(rect), topRight(rect));
    }

    /**
     * @param rect The Rectangle2D whose bottom edge line to get.
     * @return A Line2D tracing the bottom edge of the given rectangle.
     */
    public static Line2D bottomEdge(Rectangle2D rect) {
        return new Line2D.Double(bottomLeft(rect), bottomRight(rect));
    }

    /**
     * @param a The 1st point.
     * @param b The 2nd point.
     * @return The result of the component-wise addition a + b.
     */
    public static Point2D add(Point2D a, Point2D b) {
        return new Point2D.Double(a.getX() + b.getX(), a.getY() + b.getY());
    }

    /**
     * @param a The 1st point.
     * @param b The 2nd point.
     * @return The result of the component-wise subtraction a - b.
     */
    public static Point2D sub(Point2D a, Point2D b) {
        return new Point2D.Double(a.getX() - b.getX(), a.getY() - b.getY());
    }

    /**
     * @param a A scalar value.
     * @param b The point.
     * @return The result of the component-wise multiplication a * b.
     */
    public static Point2D mul(double a, Point2D b) {
        return new Point2D.Double(a * b.getX(), a * b.getY());
    }

    /**
     * @param a The 1st point.
     * @param b The 2nd point.
     * @return The magnitude of the cross product between a and b if we consider them to be 3D vectors with z=0.
     */
    public static double cross(Point2D a, Point2D b) {
        return a.getX() * b.getY() - a.getY() * b.getX();
    }

    /**
     * @param a The 3x3 matrix - must be a float[3][3].
     * @param b The 3x2 matrix - must be a float[3][2].
     * @return The 3x2 matrix resulting from the matrix multiplication of a * b.
     */
    public static float[][] multiplyMatrix3x3WithMatrix3x2(float[][] a, float[][] b) {
        float[][] r = new float[3][2];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 2; ++j) {
                r[i][j] = a[i][0] * b[0][j] + a[i][1] * b[1][j] + a[i][2] * b[2][j];
            }
        }
        return r;
    }

    /**
     * @param x The value to clamp.
     * @param min The minimum bound of the value.
     * @param max The maximum bound of the value.
     * @return max if x > max, min if x < min, or x if min <= x <= max.
     */
    public static float clamp(float x, float min, float max) {
        return Math.min(Math.max(x, min), max);
    }

    /**
     * @param x The value to saturate.
     * @return 1 if x > 1, 0 if x < 0, or x if 0 <= x <= 1.
     */
    public static float saturate(float x) {
        return clamp(x, 0, 1);
    }

    /**
     * @param from The starting point of the interpolation.
     * @param to The ending point of the interpolation.
     * @param amount The amount to interpolate by - this *should* be in [0,1] in order to have the desired effect.
     * @return A value obtained by linear interpolation from the given value to the given value.
     */
    public static float lerp(float from, float to, float amount) {
        return from + (to - from) * amount;
    }

    /**
     * @param from The starting point of the interpolation.
     * @param to The ending point of the interpolation.
     * @param amount The amount to interpolate by - this *should* be in [0,1] in order to have the desired effect.
     * @return A Color obtained by linear interpolation from the given Color to the given Color.
     */
    public static Color lerp(Color from, Color to, double amount) {
        float r0 = from.getRed() / 255.0f;
        float g0 = from.getGreen() / 255.0f;
        float b0 = from.getBlue() / 255.0f;
        float r1 = to.getRed() / 255.0f;
        float g1 = to.getGreen() / 255.0f;
        float b1 = to.getBlue() / 255.0f;
        float t = (float)amount;
        float r = saturate(lerp(r0, r1, t));
        float g = saturate(lerp(g0, g1, t));
        float b = saturate(lerp(b0, b1, t));
        return new Color(r, g, b);
    }

    /**
     * @param color The reference Color.
     * @param scaleAmount The amount by which the color should be made lighter or darker, which will be clamped to [0,1].
     * @return If the given color is determined to be "light", then a darker version of the color is returned, otherwise a lighter version of the color is returned.
     */
    public static Color darkerOrLighterColor(Color color, float scaleAmount) {
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
        float s = Math.max(r, Math.max(g, b));

        if (s > 0.5)
            return lerp(color, Color.BLACK, scaleAmount);
        else
            return lerp(color, Color.WHITE, scaleAmount);
    }

    /**
     * @param color The Color from which to derive the new Color.
     * @param alpha The transparency of the new Color, which will be clamped to [0,1].
     * @return A Color whose RGB is equal to the given color, but with the given alpha transparency value.
     */
    public static Color setAlpha(Color color, float alpha) {
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
        return new Color(r, g, b, alpha);
    }

    /**
     * This class only contains static methods and shouldn't ever be instantiated.
     */
    private MathUtil() {}

}