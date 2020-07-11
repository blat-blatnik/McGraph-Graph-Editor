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
  * @version 1.0
 *
 * When adding an edge, the user drags a 'fake' edge until they click on an actual other
 * node that the edge should be connected to. This edit is responsible for making this fake
 * edge undoable. So when the user is still in the process of dragging the fake edge around,
 * they should be able to undo and redo this.
 */
public class UnfinishedEdgeEdit extends AbstractUndoableEdit {

    private final Graph graph;
    private final List<Node> selectedNodes;
    private final double x;
    private final double y;

    /**
     * Creates an UnfinishedEdgeEdit that makes adding a fake edge undoable and redo-able.
     * @param graph the graph the edit is performed on
     * @param selectedNodes the nodes that the fake edges are connected to
     * @param x the x position of where the edge should point to
     * @param y the y position of where the edge should point to
     */
    public UnfinishedEdgeEdit(Graph graph, List<Node> selectedNodes, double x, double y) {
        this.graph = graph;
        this.selectedNodes = new ArrayList<>(selectedNodes);
        this.x = x;
        this.y = y;

        applyChange();
        graph.getUndoManager().addEdit(this);
    }

    /**
     * Applies the changes to the graph. In this case it adds fake edges
     * to all selected nodes. These fake edges will then also be added to
     * the graph's list of unfinished edges.
     */
    private void applyChange(){
        if (graph.isAddingEdges()) graph.stopAddingEdges();

        for (Node node: selectedNodes){
            Node fakeAnchorNode = new Node("", x, y, 0, 0);
            Edge newEdge = new Edge(node, fakeAnchorNode);
            newEdge.addObserver(graph);
            graph.getUnfinishedEdges().add(newEdge);
        }
    }

    /**
     * Overrides the redo method of AbstractUndoableEdit and contains the code for
     * redoing this action, i.e. adding the edges.
     * @throws CannotRedoException if the edit cannot be redone.
     */
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        applyChange();
    }

    /**
     * Overrides the undo method of AbstractUndoableEdit and contains the code for
     * undoing the action, i.e. stop adding the edges and delete the unfinished edges
     * from the graph.
     * @throws CannotUndoException if it cannot be undone.
     */
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        graph.stopAddingEdges();
    }
}
