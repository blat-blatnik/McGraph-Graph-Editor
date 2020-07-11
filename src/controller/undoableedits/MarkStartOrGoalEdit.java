package controller.undoableedits;

import model.Graph;
import model.Node;
import controller.Solver;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
* @version 1.0
 *
 * This edit is responsible for undoing and redoing the marking of the start and goal node that is
 * used in the solver.
 *
 * @see Solver
 */
public class MarkStartOrGoalEdit extends AbstractUndoableEdit {

    private final Graph graph;
    private final Node newStartNode;
    private final Node oldStartNode;
    private final Node newGoalNode;
    private final Node oldGoalNode;

    /**
     * Constructs an edit that marks the start and goal node of the graph.
     * @param graph the graph that is edited
     * @param newStartNode the new start node
     * @param newGoalNode the new goal node
     */
    public MarkStartOrGoalEdit(Graph graph, Node newStartNode, Node newGoalNode) {
        this.graph = graph;
        this.newStartNode = newStartNode;
        this.newGoalNode = newGoalNode;
        this.oldStartNode = graph.getStartNode();
        this.oldGoalNode = graph.getGoalNode();

        if (newStartNode != oldStartNode || newGoalNode != oldGoalNode) {
            graph.getUndoManager().addEdit(this);
            applyChange();
        }
    }

    /**
     * Applies the change to the graph, i.e. marking the start and end node.
     */
    private void applyChange(){
        graph.setStartNode(newStartNode);
        graph.setGoalNode(newGoalNode);
    }

    /**
     * Overrides the redo() method of the AbstractUndoableEdit class and contains the code to
     * redo this edit, i.e. setting the start and goal nodes.
     * @throws CannotRedoException if edit cannot be redone
     */
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        applyChange();
    }

    /**
     * Overrides the undo() method of the AbstractUndoableEdit class and contains the code to
     * redo this edit, i.e. undoing the start and goal nodes.
     * @throws CannotUndoException if edit cannot be undone
     */
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        graph.setStartNode(oldStartNode);
        graph.setGoalNode(oldGoalNode);
    }
}