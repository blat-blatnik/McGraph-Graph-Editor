package controller.undoableedits;

import model.Edge;
import model.Graph;
import model.Node;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.ArrayList;
import java.util.List;

/**
  * @version 2.1
 *
 * This class takes care of all edits that relate to adding nodes, edges or both.
 * It adds these to the graph, and removes them in the undo method.
 */
public class AddNodesAndEdgesEdit extends AbstractUndoableEdit {

    private final Graph graph;
    private final List<Node> nodes;
    private final List<Edge> edges;

    /**
     * Constructs an edit with the necessary parameters. It can be called with all
     * parameters, or either with only edges or nodes and a null reference for the other
     * parameter.
     * @param graph the graph that is edited
     * @param nodes the nodes that could be edited
     * @param edges the edges that could be edited
     */
    public AddNodesAndEdgesEdit(Graph graph, List<Node> nodes, List<Edge> edges) {
        this.graph = graph;

        this.nodes = (nodes == null) ? null : new ArrayList<>(nodes);
        this.edges = (edges == null) ? null : new ArrayList<>(edges);

        if (nodes != null && edges != null) {
            for (Edge edge: this.graph.getEdges()) {
                for (Node node: nodes) {
                    if (edge.connectsTo(node) && !edges.contains(edge))
                        edges.add(edge);
                }
            }
        }

        graph.getUndoManager().addEdit(this);
        performAction();
    }

    /**
     * Performs the actual edit. If nodes are provided in the edit, it adds nodes,
     * if edges are provided, it adds edges. If both are provided, it adds both to the
     * graph model.
     */
    private void performAction(){

        if (nodes != null) {
            graph.clearSelectedNodes();
            for (Node node : nodes) {
                graph.add(node);
                graph.select(node);
            }
        }

        if (edges != null){
            graph.clearSelectedEdges();
            for (Edge edge : edges) {
                graph.add(edge);
                graph.select(edge);
            }
        }

    }

    /**
     * Overrides the redo method of the AbstractUndoableEdit class and contains the code to
     * redo this edit, i.e. adding nodes and/or edges.
     * @throws CannotRedoException if edit cannot be redone
     */
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        performAction();

    }

    /**
     * Overrides the undo method of the AbstractUndoableEdit class and contains the code to
     * undo this edit, i.e. removing nodes and/or edges.
     * @throws CannotUndoException if edit cannot be undone
     */
    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        if (nodes != null){
            for (Node node : nodes) graph.remove(node);
        }

        if (edges != null){
            for (Edge edge : edges) graph.remove(edge);
        }

        graph.clearSelectedNodes();
    }
}
