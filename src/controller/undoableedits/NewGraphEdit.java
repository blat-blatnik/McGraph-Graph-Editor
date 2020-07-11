package controller.undoableedits;

import model.Graph;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * @author Jana
 * @version 1.0
 *
 * This makes emptying a graph an undoable action. Undo brings the old graph back and redo clears the graph again.
 *
 * @see Graph
 */
public class NewGraphEdit extends AbstractUndoableEdit {

    private final Graph oldGraph;
    private final Graph graph;

    /**
     * Constructs a NewGraphEdit, stores the old graph, clears the graph and adds itself to the graph undo manager.
     * @param graph graph that is edited
     */
    public NewGraphEdit(Graph graph) {
        this.graph = graph;
        this.oldGraph = new Graph(graph);

        this.graph.clear();
        this.graph.getUndoManager().addEdit(this);
        this.graph.setChanged();
        this.graph.notifyObservers();
    }

    /**
     * Contains the code for undoing the action, i.e. setting the graph data to the old graph.
     * @throws CannotUndoException if action cannot be undone
     */
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        this.graph.set(oldGraph);
    }

    /**
     * Redoes the action, i.e. clears the graph.
     * @throws CannotRedoException if it cannot be redone
     */
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        this.graph.clear();
    }
}
