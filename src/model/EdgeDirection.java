package model;

/**
 * @version 1.0
 * @author Boris
 *
 * This enum encodes the directionality of an Edge.
 *
 * @see Edge
 * @see Node
 */
public enum EdgeDirection {
    DIRECTED_TO_NODE_1,
    DIRECTED_TO_NODE_2,
    DIRECTED_TO_BOTH_NODES;

    /**
     * @return Whether this EdgeDirection directs to the first node.
     */
    public boolean directsToNode1() {
        return this == DIRECTED_TO_NODE_1 || this == DIRECTED_TO_BOTH_NODES;
    }

    /**
     * @return Whether this EdgeDirection directs to the second node.
     */
    public boolean directsToNode2() {
        return this == DIRECTED_TO_NODE_2 || this == DIRECTED_TO_BOTH_NODES;
    }

}