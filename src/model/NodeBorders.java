package model;

/**
 * @author Boris
 * @version 2.0
 *
 * This enum represents the borders of a Node's shape that can be selected for resizing purposes. It defines the borders
 * in the 4 basic directions: LEFT, RIGHT, TOP, and BOTTOM, but it also encodes combinations that meet in one of the
 * corners of the shape: TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT and BOTTOM_RIGHT.
 *
 * This is because the user can drag any border of a Node to resize it only in 1 dimension, or they can drag any corner
 * to resize the Node in both directions.
 *
 * @see Node
 * @see controller.SelectionController
 */
public enum NodeBorders {
    NONE,
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT;

    /**
     * @return Whether these NodeBorders contain the left border.
     */
    public boolean isLeft() {
        return this == LEFT || this == TOP_LEFT || this == BOTTOM_LEFT;
    }

    /**
     * @return Whether these NodeBorders contain the right border.
     */
    public boolean isRight() {
        return this == RIGHT || this == TOP_RIGHT || this == BOTTOM_RIGHT;
    }

    /**
     * @return Whether these NodeBorders contain the top border.
     */
    public boolean isTop() {
        return this == TOP || this == TOP_LEFT || this == TOP_RIGHT;
    }

    /**
     * @return Whether these NodeBorders contain the bottom border.
     */
    public boolean isBottom() {
        return this == BOTTOM || this == BOTTOM_LEFT || this == BOTTOM_RIGHT;
    }

    /**
     * @return A new set of NodeBorders that encode the left border, in addition to the top or bottom border, and replacing the right border.
     */
    public NodeBorders setLeft() {
        switch (this) {
            default:
            case NONE:
            case LEFT:
            case RIGHT:
                return LEFT;
            case TOP:
            case TOP_LEFT:
            case TOP_RIGHT:
                return TOP_LEFT;
            case BOTTOM:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                return BOTTOM_LEFT;
        }
    }

    /**
     * @return A new set of NodeBorders that encode the right border, in addition to the top or bottom border, and replacing the left border.
     */
    public NodeBorders setRight() {
        switch (this) {
            default:
            case NONE:
            case LEFT:
            case RIGHT:
                return RIGHT;
            case TOP:
            case TOP_LEFT:
            case TOP_RIGHT:
                return TOP_RIGHT;
            case BOTTOM:
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                return BOTTOM_RIGHT;
        }
    }

    /**
     * @return A new set of NodeBorders that encode the top border, in addition to the left or right border, and replacing the bottom border.
     */
    public NodeBorders setTop() {
        switch (this) {
            default:
            case NONE:
            case TOP:
            case BOTTOM:
                return TOP;
            case TOP_LEFT:
            case BOTTOM_LEFT:
            case LEFT:
                return TOP_LEFT;
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
            case RIGHT:
                return TOP_RIGHT;
        }
    }

    /**
     * @return A new set of NodeBorders that encode the bottom border, in addition to the left or right border, and replacing the top border.
     */
    public NodeBorders setBottom() {
        switch (this) {
            default:
            case NONE:
            case TOP:
            case BOTTOM:
                return BOTTOM;
            case TOP_LEFT:
            case BOTTOM_LEFT:
            case LEFT:
                return BOTTOM_LEFT;
            case TOP_RIGHT:
            case BOTTOM_RIGHT:
            case RIGHT:
                return BOTTOM_RIGHT;
        }
    }
}