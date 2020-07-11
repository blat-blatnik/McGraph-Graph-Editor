package controller.undoableedits;

import model.*;
import utils.Action;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.util.ArrayList;
import java.util.List;

/**
  * @version 3.0
 *
 * This class handles most edits related to edges and nodes, for example changing the color,
 * the weight, the name of a node, etc. Adding and removing edges is not
 * handled in here, as it can only handle edits on existing edges.
 * This is the template for both NodeEdits and EdgeEdits, which both inherit from this.
 *
 * @see NodeEdit
 * @see EdgeEdit
 */
public class NodeAndEdgeEdit extends AbstractUndoableEdit {

    private final List<Node> nodesToChange;
    private final List<Edge> edgesToChange;
    private final List<NodeData> newNodeData;
    private final List<EdgeData> newEdgeData;
    private final List<NodeData> oldNodeData;
    private final List<EdgeData> oldEdgeData;

    /**
     * Constructs an edit with all important data and calls the other constructor with it.
     * @param graph graph which is edited
     * @param nodesToChange nodes that should be edited
     * @param edgesToChange edges that should be edited
     * @param nodeAction the action that is to be done on nodes
     * @param edgeAction the action that is to be done on edges
     */
    public NodeAndEdgeEdit(
            Graph graph,
            List<Node> nodesToChange,
            List<Edge> edgesToChange,
            Action<Node> nodeAction,
            Action<Edge> edgeAction)
    {
        this(graph, nodesToChange, nodesToChange, edgesToChange, edgesToChange, nodeAction, edgeAction);
    }

    /**
     * Constructs an edit with all important data. The constructor performs the action that is given
     * in the parameters and checks whether an actual change has occurred. If so, it adds this edit to the
     * UndoManager of the graph.
     *
     * @param graph graph which is edited
     * @param nodesToChange nodes that should be edited
     * @param edgesToChange edges that should be edited
     * @param oldEdges old edges stored for undo
     * @param oldNodes old nodes stored for undo
     * @param nodeAction the action that is to be done on nodes
     * @param edgeAction the action that is to be done on edges
     */
    public NodeAndEdgeEdit(
            Graph graph,
            List<Node> nodesToChange,
            List<Node> oldNodes,
            List<Edge> edgesToChange,
            List<Edge> oldEdges,
            Action<Node> nodeAction,
            Action<Edge> edgeAction)
    {
        this.nodesToChange = new ArrayList<>();
        this.edgesToChange = new ArrayList<>();
        this.newNodeData = new ArrayList<>();
        this.newEdgeData = new ArrayList<>();
        this.oldNodeData = new ArrayList<>();
        this.oldEdgeData = new ArrayList<>();

        if (nodesToChange.size() != oldNodes.size())
            return;
        if (edgesToChange.size() != oldEdges.size())
            return;

        // stores copies of the node data in lists, storing the old and new data.
        for (int i = 0; i < nodesToChange.size(); ++i) {
            Node oldNode = oldNodes.get(i);
            Node newNode = nodesToChange.get(i);

            NodeData oldData = new NodeData(oldNode.getActualData());
            nodeAction.doAction(newNode);
            NodeData newData = new NodeData(newNode.getActualData());

            if (!newData.equals(oldData)) {
                this.nodesToChange.add(newNode);
                newNodeData.add(newData);
                oldNodeData.add(oldData);
            }
        }

        // stores copies of the edge data in lists, storing the old and new data.
        for (int i = 0; i < edgesToChange.size(); ++i) {
            Edge oldEdge = oldEdges.get(i);
            Edge newEdge = edgesToChange.get(i);

            EdgeData oldData = new EdgeData(oldEdge.getActualData());
            edgeAction.doAction(newEdge);
            EdgeData newData = new EdgeData(newEdge.getActualData());

            if (!newData.equals(oldData)) {
                this.edgesToChange.add(newEdge);
                newEdgeData.add(newData);
                oldEdgeData.add(oldData);
            }
        }

        // checks if changes have occurred and adds them to the UndoManager.
        int numChanges = this.nodesToChange.size() + this.edgesToChange.size();
        if (numChanges > 0) {
            graph.getUndoManager().addEdit(this);
            graph.setChanged();
            graph.notifyObservers();
        }
    }

    /**
     * Overrides the redo() method of the AbstractUndoableEdit class and contains the code to
     * redo this edit, which can be for example changing the color of nodes and edges. This is done by
     * setting the data of the actual node/edge to the data that was changed in the constructor (newData).
     * @throws CannotRedoException if edit cannot be redone
     */
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        for (int i = 0; i < nodesToChange.size(); i++) {
            Node nodeToChange = nodesToChange.get(i);
            NodeData newData = newNodeData.get(i);
            nodeToChange.setData(newData);
        }
        for (int i = 0; i < edgesToChange.size(); i++) {
            Edge edgeToChange = edgesToChange.get(i);
            EdgeData newData = newEdgeData.get(i);
            edgeToChange.setData(newData);
        }
    }

    /**
     * Overrides the undo() method of the AbstractUndoableEdit class and contains the code to
     * redo this edit, i.e. changing a color back to the old one. This is done by setting the
     * data of the actual node/edge to the old data that was stored.
     * @throws CannotUndoException if edit cannot be undone
     */
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        for (int i = 0; i < nodesToChange.size(); i++) {
            Node nodeToChange = nodesToChange.get(i);
            NodeData oldData = oldNodeData.get(i);
            nodeToChange.setData(oldData);
        }
        for (int i = 0; i < edgesToChange.size(); i++) {
            Edge edgeToChange = edgesToChange.get(i);
            EdgeData oldData = oldEdgeData.get(i);
            edgeToChange.setData(oldData);
        }
    }

}