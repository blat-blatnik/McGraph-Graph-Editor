package model;

import controller.Solver;
import utils.GraphIO;

import java.awt.geom.Rectangle2D;
import java.util.*;

/**
  * @version 5.0
 *
 * This is the model for a graph. It is the main component of this model and keeps track of everything
 * that is happening in a graph, e.g. it stores nodes, edges, which ones are selected etc.
 * GraphModel observes edges and nodes, so that it can then notify the panel whenever its state changes.
 * It also has an UndoManager that can keep track of the edits and enables undoable and redo-able actions.
 *
 * @see Node
 * @see Edge
 * @see GraphUndoManager
 */
public class Graph extends Observable implements Observer {

    private final List<Node> nodes;
    private final List<Edge> edges;
    private final List<Node> selectedNodes;
    private final List<Edge> selectedEdges;
    private final List<Edge> unfinishedEdges;
    private Node hoveredNode;
    private Edge hoveredEdge;
    private Node startNode;
    private Node goalNode;
    private String filename;
    private GraphUndoManager undoManager;
    private int indexOfLastEditSinceSave;
    private boolean nodeClicked;

    /**
     * Constructs a new graph model with initialising values.
     *
     * @see GraphUndoManager
     * @see Node
     * @see Edge
     */
    public Graph() {
        super();
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        selectedNodes = new ArrayList<>();
        selectedEdges = new ArrayList<>();
        unfinishedEdges = new ArrayList<>();
        undoManager = new GraphUndoManager();
        undoManager.setLimit(16384);
        clear();
    }

    /**
     * This constructor can be used to load a graph from a filepath (see requirements part 1.2.).
     * @param filepath path to the file that will be loaded from
     */
    public Graph(String filepath) {
        this();
        GraphIO.loadGraph(this, filepath);
    }

    /**
     * Copy constructor: this is used for saving a backup o the graph when saving it into a file.
     * @param other other GraphModel that will be copied.
     */
    public Graph(Graph other) {
        this();
        set(other);
    }

    /**
     * Clears all data and sets it back to initial values.
     */
    public void clear() {
        undoManager.discardAllEdits();
        nodes.clear();
        edges.clear();
        selectedNodes.clear();
        selectedEdges.clear();
        unfinishedEdges.clear();
        hoveredNode = null;
        hoveredEdge = null;
        startNode = null;
        goalNode = null;
        filename = "";
        indexOfLastEditSinceSave = -1;
        nodeClicked = false;

        setChanged();
        notifyObservers();
    }

    /**
     * Sets the data of the graph model to data provided by a different graph model.
     * @param other the other graph model that provides the data.
     */
    public void set(Graph other) {
        clear();
        nodes.addAll(other.nodes);
        edges.addAll(other.edges);
        selectedNodes.addAll(other.selectedNodes);
        selectedEdges.addAll(other.selectedEdges);
        unfinishedEdges.addAll(other.unfinishedEdges);
        hoveredNode = other.hoveredNode;
        hoveredEdge = other.hoveredEdge;
        startNode = other.startNode;
        goalNode = other.goalNode;
        filename = other.filename;
        undoManager = other.undoManager;
        indexOfLastEditSinceSave = other.indexOfLastEditSinceSave;
        nodeClicked = other.nodeClicked;
        setChanged();
        notifyObservers();
    }

    /**
     * Adds a node to the graph model.
     * @param node node that is added
     */
    public void add(Node node) {
        nodes.add(node);
        node.addObserver(this);
        setChanged();
        notifyObservers();
    }

    /**
     * Adds an edge to the graph model.
     * @param edge edge that is added
     */
    public void add(Edge edge) {
        edges.add(edge);
        edge.addObserver(this);
        setChanged();
        notifyObservers();
    }

    /**
     * Removes a node from the graph model. Also removes connected edges.
     * @param node node that is removed
     */
    public void remove(Node node) {
        node.deleteObserver(this);

        nodes.remove(node);
        edges.removeIf(edge -> edge.connectsTo(node));
        selectedNodes.remove(node);
        selectedEdges.removeIf(edge -> edge.connectsTo(node));

        if (startNode == node)
            startNode = null;
        if (goalNode == node)
            goalNode = null;

        setChanged();
        notifyObservers();
    }

    /**
     * Removes an edge to the graph model.
     * @param edge node that is added
     */
    public void remove(Edge edge) {
        edge.deleteObserver(this);
        edges.remove(edge);
        selectedEdges.remove(edge);
        setChanged();
        notifyObservers();
    }

    /**
     * Moves the selected nodes by the given coordinates. Calls the related method in nodes.
     * @param deltaX number by which the x-coordinates will be moved
     * @param deltaY number by which the y-coordinates will be moved
     */
    public void moveSelectedNodesBy(double deltaX, double deltaY) {
        for (Node node : getSelectedNodes()) {

            NodeBorders selectedBorder = node.getSelectedBorders();
            if (selectedBorder == NodeBorders.NONE) {
                node.moveBy(deltaX, deltaY);
                continue;
            }

            double dX = 0;
            double dY = 0;
            double dW = 0;
            double dH = 0;

            if (selectedBorder.isLeft()) {
                dX += deltaX;
                dW -= deltaX;
            }
            if (selectedBorder.isTop()) {
                dY += deltaY;
                dH -= deltaY;
            }

            if (selectedBorder.isRight())
                dW += deltaX;
            if (selectedBorder.isBottom())
                dH += deltaY;

            node.modifyBounds(dX, dY, dW, dH);
        }
    }

    /**
     * Moves the selected edges by given coordinates. Calls the related method in edges.
     * @param deltaX number by which the x-coordinates will be moved
     * @param deltaY number by which the y-coordinates will be moved
     */
    public void moveSelectedEdgesBy(double deltaX, double deltaY) {
        for (Edge edge : getSelectedEdges())
            edge.moveActualWeightPointBy(deltaX, deltaY);
    }

    /**
     * Selects a node on the graph.
     * @param node node that is selected
     * @see Node
     */
    public void select(Node node) {
        if (node == null)
            return;


        if (!selectedNodes.contains(node)) {
            selectedNodes.add(node);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Selects an edge on the graph.
     * @param edge edge that is selected
     * @see Edge
     */
    public void select(Edge edge) {
        if (edge == null)
            return;

        if (!selectedEdges.contains(edge)) {
            selectedEdges.add(edge);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Deselects an node on the graph.
     * @param node node that is selected
     * @see Node
     */
    public void deselect(Node node) {
        if (selectedNodes.contains(node)) {
            selectedNodes.remove(node);
            unfinishedEdges.removeIf(edge -> edge.connectsTo(node));
            node.setSelectedBorders(NodeBorders.NONE);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Deselects an edge on the graph.
     * @param edge edge that is selected
     * @see Edge
     */
    public void deselect(Edge edge) {
        if (selectedEdges.contains(edge)) {
            selectedEdges.remove(edge);
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Stops adding unfinished edges.
     */
    public void stopAddingEdges() {
        if (isAddingEdges()) {
            // deleting observer before removing the edges
            for (Edge edge: unfinishedEdges){
                edge.deleteObserver(this);
            }
            unfinishedEdges.clear();
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return nodes of the graph.
     */
    public List<Node> getNodes(){
        return nodes;
    }

    /**
     * @return edges of the graph
     */
    public List<Edge> getEdges(){
        return edges;
    }

    /**
     * @return selected nodes of the graph
     */
    public List<Node> getSelectedNodes(){
        return selectedNodes;
    }

    /**
     * @return selected edges of the graph
     */
    public List<Edge> getSelectedEdges(){
        return selectedEdges;
    }

    /**
     * @return number of selected nodes of the graph
     */
    public int numSelectedNodes(){
        return selectedNodes.size();
    }

    /**
     * @return number of selected edges of the graph
     */
    public int numSelectedEdges(){
        return selectedEdges.size();
    }

    /**
     * Checks whether a node is selected or not.
     * @param node node that is checked for selection
     * @return boolean indicating whether that node is selected
     */
    public boolean isSelected(Node node) {
        return selectedNodes.contains(node);
    }

    /**
     * Checks whether an edge is selected or not.
     * @param edge edge that is checked for selection
     * @return boolean indicating whether that edge is selected
     */
    public boolean isSelected(Edge edge) {
        return selectedEdges.contains(edge);
    }

    /**
     * Clears the list of selected nodes.
     */
    public void clearSelectedNodes() {
        if (!selectedNodes.isEmpty()) {
            for (Node node : selectedNodes)
                node.setSelectedBorders(NodeBorders.NONE);

            unfinishedEdges.removeIf(edge -> selectedNodes.contains(edge.getNode1()));

            selectedNodes.clear();
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Clears the list of selected edges.
     */
    public void clearSelectedEdges() {
        if (!selectedEdges.isEmpty()) {
            selectedEdges.clear();
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return node that the user hovers over
     */
    public Node getHoveredNode() {
        return hoveredNode;
    }

    /**
     * Sets the hovered node.
     * @param node that the user hovers over
     */
    public void setHoveredNode(Node node) {
        if (hoveredNode != node) {
            hoveredNode = node;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return edge that the user hovers over
     */
    public Edge getHoveredEdge() {
        return hoveredEdge;
    }

    /**
     * Sets the hovered edge.
     * @param edge that the user hovers over
     */
    public void setHoveredEdge(Edge edge) {
        if (hoveredEdge != edge) {
            hoveredEdge = edge;
            setChanged();
            notifyObservers();

        }
    }

    /**
     * @return name of the graph (filename without extension).
     */
    public String getName() {
        return GraphIO.getFilenameWithoutExtension(filename);
    }

    /**
     * @return filename of the graph.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename to a new name.
     * @param newFilename new name of the file
     */
    public void setFilename(String newFilename) {
        if (!newFilename.equals(filename)) {
            filename = newFilename;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return boolean indicating whether the user is adding edges at the moment.
     */
    public boolean isAddingEdges() {
        return !unfinishedEdges.isEmpty();
    }

    /**
     * @return gets unfinished edges.
     */
    public List<Edge> getUnfinishedEdges() {
        return unfinishedEdges;
    }

    /**
     * @return boolean indicating whether there are unsaved changes made.
     */
    public boolean hasUnsavedChanges() {
        return indexOfLastEditSinceSave != undoManager.getUndoPointer();
    }

    /**
     * Resets the index of last edit since saved.
     */
    public void clearChangedSinceLastSave() {
        int newUndoPointer = undoManager.getUndoPointer();
        if (indexOfLastEditSinceSave != newUndoPointer) {
            indexOfLastEditSinceSave = newUndoPointer;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return bounds of the graph.
     */
    public Rectangle2D getBounds() {
        Rectangle2D.Double bounds = new Rectangle2D.Double();
        if (!nodes.isEmpty())
            bounds.setRect(nodes.get(0).getVisualBounds());
        for (Node node : getNodes())
            bounds.add(node.getVisualBounds());
        return bounds;
    }

    /**
     * @return the UndoManager
     */
    public GraphUndoManager getUndoManager() {
        return undoManager;
    }

    /**
     * @return start node for search algorithm.
     * @see Solver
     */
    public Node getStartNode() {
        return startNode;
    }

    /**
     * @return goal node for search algorithm.
     * @see Solver
     */
    public Node getGoalNode() {
        return goalNode;
    }

    /**
     * Sets the start node.
     * @param node node that is set to start.
     */
    public void setStartNode(Node node) {
        if (startNode != node) {
            startNode = node;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * Sets the goal node.
     * @param node node that is set to goal.
     */
    public void setGoalNode(Node node) {
        if (goalNode != node) {
            goalNode = node;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * @return boolean indicating whether a node was clicked
     */
    public boolean nodeWasClicked() {
        return nodeClicked;
    }

    /**
     * Sets the nodeClicked to a boolean value.
     * @param value indicating whether a node was clicked or not
     */
    public void setNodeClicked(boolean value) {
        if (nodeClicked != value) {
            nodeClicked = value;
            setChanged();
            notifyObservers();
        }
    }

    /**
     * As GraphModel is observing nodes and edges, whenever the graph model is updated,
     * it facilitates these updates and notifies its observers, the most important ones being
     * the graph panel and the buttons in order to set them to visible.
     * @param o Observable
     * @param arg general message passed to observers
     */
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers();
    }

    /**
     * Sets this graph model as having changed - we need to access this method outside of GraphModel so we had to
     * redeclare it.
     */
    @Override
    public void setChanged() {
        super.setChanged();
    }
}