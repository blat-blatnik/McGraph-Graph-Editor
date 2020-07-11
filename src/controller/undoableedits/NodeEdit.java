package controller.undoableedits;

import model.Graph;
import model.Node;
import utils.Action;

import java.util.ArrayList;
import java.util.List;

/**
  * @version 3.0
 *
 * This class inherits from NodeAndEdgeEdit and provides the necessary information to construct a NodeEdit.
 * The actual edit is handled in the parent class.
 *
 * @see NodeAndEdgeEdit
 */
public class NodeEdit extends NodeAndEdgeEdit {

    /**
     * Constructs an NodeEdit with reference to the graph it edits, the edges that need to be changed, a reference
     * to old edges and an action that can be carried out. It passes empty lists for potential lists of edges to the
     * constructor it inherits from.
     * @param graph graph that is edited
     * @param nodesToChange nodes that need to be changed
     * @param oldNodes old nodes that can be used for undo
     * @param action action that can be carried out
     */
    public NodeEdit(Graph graph, List<Node> nodesToChange, List<Node> oldNodes, Action<Node> action) {
        super(graph, nodesToChange, oldNodes, new ArrayList<>(), new ArrayList<>(), action, null);
    }

    /**
     * Constructs an NodeEdit with reference to the graph it edits, the edges that need to be changed and an action
     * that can be carried out. It passes empty lists for potential lists of edges to the constructor it inherits from.
     * @param graph graph that is edited
     * @param selectedNodes nodes that are selected
     * @param action action that can be carried out
     */
    public NodeEdit(Graph graph, List<Node> selectedNodes, Action<Node> action) {
        this(graph, selectedNodes, selectedNodes, action);
    }
}