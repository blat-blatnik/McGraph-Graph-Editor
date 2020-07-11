package controller.undoableedits;

import model.Edge;
import model.Graph;
import utils.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jana
 * @author Boris
 * @version 2.0
 *
 * This class inherits from NodeAndEdgeEdit and provides the necessary information to construct an EdgeEdit.
 * The actual edit is handled in the parent class.
 *
 * @see NodeAndEdgeEdit
 */
public class EdgeEdit extends NodeAndEdgeEdit {

    /**
     * Constructs an EdgeEdit with reference to the graph it edits, the edges that need to be changed, a reference
     * to old edges and an action that can be carried out. It passes empty lists for potential lists of nodes to the
     * constructor it inherits from.
     * @param graph graph that is edited
     * @param edgesToChange edges that need to be changed
     * @param oldEdges old edges that can be used for undo
     * @param action action that can be carried out
     */
    public EdgeEdit(Graph graph, List<Edge> edgesToChange, List<Edge> oldEdges, Action<Edge> action) {
        super(graph, new ArrayList<>(), new ArrayList<>(), edgesToChange, oldEdges, null, action);
    }

    /**
     * Constructs an EdgeEdit with reference to the graph it edits, the edges that need to be changed and an action that can be carried out.
     * @param graph graph that is edited
     * @param edgesToChange edges that need to be changed
     * @param action action that can be carried out
     */
    public EdgeEdit(Graph graph, List<Edge> edgesToChange, Action<Edge> action) {
        this(graph, edgesToChange, edgesToChange, action);
    }
}