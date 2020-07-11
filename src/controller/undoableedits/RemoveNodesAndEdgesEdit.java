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
 * @author Jana
 * @version 2.0
 *
 * This class takes care of all edits that relate to removing nodes, edges or both.
 * It removes these from the graph, and adds them back in the undo method.
 *
 * @see Node
 * @see Edge
 */
public class RemoveNodesAndEdgesEdit extends AbstractUndoableEdit {

    private final Graph graph;
    private final List<Node> selectedNodes;
    private final List<Edge> selectedEdges;

    /**
     * Constructs an edit with reference to the graph. Selected nodes and edges are then stored from
     * the graph reference.
     * @param graph The GraphModel that this edit will modify.
     */
    public RemoveNodesAndEdgesEdit(Graph graph){
        this.graph = graph;

        this.selectedNodes = new ArrayList<>(this.graph.getSelectedNodes());
        this.selectedEdges = new ArrayList<>(this.graph.getSelectedEdges());

        for (Edge edge: this.graph.getEdges()){
            for (Node node: selectedNodes){
                if (edge.connectsTo(node) && !selectedEdges.contains(edge))
                    selectedEdges.add(edge);
            }
        }

        graph.getUndoManager().addEdit(this);
        applyChange();
    }

    /**
     * Performs the action of the edit, i.e. actually removes nodes and edges from the graph.
     * @see Node
     * @see Edge
     */
    private void applyChange(){
        for (Node node : selectedNodes)
            graph.remove(node);
        for (Edge edge : selectedEdges)
            graph.remove(edge);
    }

    /**
     * Overrides the redo() method of the AbstractUndoableEdit class and contains the code to
     * redo this edit, i.e. removing nodes and/or edges.
     * @throws CannotRedoException if edit cannot be redone
     */
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        applyChange();
    }

    /**
     * Overrides the undo() method of the AbstractUndoableEdit class and contains the code to
     * redo this edit, i.e. adding nodes and/or edges.
     * @throws CannotUndoException if edit cannot be undone
     */
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        for (Node node : selectedNodes)
            graph.add(node);
        for (Edge edge : selectedEdges)
            graph.add(edge);
    }
}
