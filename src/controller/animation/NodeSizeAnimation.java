package controller.animation;

import model.Node;
import utils.MathUtil;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

/**
 * @author Boris
 * @version 1.0
 *
 * Represents an Animation that expands and then shrinks the rectangle bounds of Nodes. This Animation plays when the
 * user selects a Node. The Nodes will first expand, and then shrink back to their original size from before the
 * Animation started.
 *
 * @see Animation
 * @see Node
 */
public class NodeSizeAnimation extends Animation {

    /**
     * Constructs a NodeSizeAnimation for a given list of Nodes, how much they should expand/shrink by, and the duration
     * of the Animation.
     *
     * @param nodes The List of Nodes to expand or shrink.
     * @param peakDeltaWidth The maximum difference in width of the Nodes while the Animation is playing.
     * @param peakDeltaHeight The maximum difference in height of the Nodes while the Animation is playing.
     * @param duration The duration of this Animation.
     */
    public NodeSizeAnimation(List<Node> nodes, double peakDeltaWidth, double peakDeltaHeight, double duration) {
        super(duration, animation -> {
            double t = animation.getCurrentTime() / animation.getDuration();
            t = 1 - Math.abs(2 * t - 1);
            for (Node node : nodes) {
                Rectangle2D bounds = MathUtil.growRectangle(node.getActualBounds(),
                        t * peakDeltaWidth, t * peakDeltaHeight);
                node.setVisualBounds(bounds);
            }
        });
    }

    /**
     * Constructs a NodeSizeAnimation for a single Node.
     *
     * @param node The Node to expand or shrink.
     * @param peakDeltaWidth The maximum difference in width of the Node while the Animation is playing.
     * @param peakDeltaHeight The maximum difference in height of the Node while the Animation is playing.
     * @param duration The duration of this Animation.
     */
    public NodeSizeAnimation(Node node, double peakDeltaWidth, double peakDeltaHeight, double duration) {
        this(Arrays.asList(node), peakDeltaWidth, peakDeltaHeight, duration);
    }

}