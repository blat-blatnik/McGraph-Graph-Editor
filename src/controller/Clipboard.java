package controller;

import controller.undoableedits.AddNodesAndEdgesEdit;
import model.Edge;
import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * @author Boris
 * @author Jana
 * @version 2.0
 *
 * This class represents the Clipboard that stores all Nodes and Edges that were copied/cut and can be pasted back.
 * It follows the Singleton Design Pattern, as this graphModel should only ever have one clipboard, i.e. there
 * should always only be one instance of this class.
 *
 * @see Node
 * @see Edge
 */
public class Clipboard extends Observable {

    private final List<Node> storedNodes;
    private final List<Edge> storedEdges;

    /**
     * This class follows the Singleton pattern and should only be instantiated once,
     * therefore the constructor is private, so it cannot be instantiated outside of this class.
     */
    private Clipboard() {
        storedNodes = new ArrayList<>();
        storedEdges = new ArrayList<>();
    }

    /**
     * This way of creating a Singleton class is thread-safe and ensures lazy loading.
     * The instance is only loaded when accessed through the getInstance() method.
     */
    private static class SingletonHelper {
        private static final Clipboard INSTANCE = new Clipboard();
    }

    /**
     * Gets the only instance of the clipboard.
     * @return Clipboard instance
     */
    public static Clipboard getInstance(){
        return SingletonHelper.INSTANCE;
    }


    /**
     * Performs a deep copy of the given list of Nodes and Edges, storing them in the contents of the Clipboard. The
     * previous contents of the Clipboard are discarded. Only Edges that connect to Nodes that are BOTH in the given
     * list of Nodes will actually be stored.
     *
     * @param nodes The list of Nodes to store.
     * @param edges The list of Edges to store.
     * @see Node
     * @see Edge
     */
    public void store(List<Node> nodes, List<Edge> edges) {
        storedNodes.clear();
        storedEdges.clear();

        for (Node node : nodes){
            storedNodes.add(new Node(node));
        }

        for (Edge edge : edges) {
            int node1Index = nodes.indexOf(edge.getNode1());
            int node2Index = nodes.indexOf(edge.getNode2());
            if (node1Index >= 0 && node2Index >= 0) {
                Edge copy = new Edge(edge);
                copy.setNode1(storedNodes.get(node1Index));
                copy.setNode2(storedNodes.get(node2Index));
                storedEdges.add(copy);
            }
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Pastes the nodes and edges that are stored in the clipboard into the graph. Doing so it performs
     * another deep copy of the nodes and/or edges, so that the nodes and/or edges that are added actually
     * refer to different instances. The pasted nodes are also moved a little, so that they do not cover up
     * the node/edges that they were copied from.
     * @param graph the graph that the nodes and/or edges will be added to.
     */
    public void paste(Graph graph){
        // We have to be very careful with the references here - always make sure that the edges
        // that are added to the graph actually refer to the nodes that were added to graph, and not the nodes from
        // the clipboard.

        List<Node> pastedNodes = new ArrayList<>(storedNodes.size());
        List<Edge> pastedEdges = new ArrayList<>(storedEdges.size());

        for (Node node : storedNodes) {
            Node pastedNode = new Node(node);

            // Move nodes by a little bit so that they don't cover up the old nodes.
            pastedNode.moveBy(4, 4);
            pastedNodes.add(pastedNode);
        }

        for (Edge edge : storedEdges) {
            Edge pastedEdge = new Edge(edge);
            int node1Index = storedNodes.indexOf(edge.getNode1());
            int node2Index = storedNodes.indexOf(edge.getNode2());
            pastedEdge.setNode1(pastedNodes.get(node1Index));
            pastedEdge.setNode2(pastedNodes.get(node2Index));

            // Move edges just like nodes, so that the new edges are not painted
            // on top of the old ones.
            pastedEdge.moveActualWeightPointBy(4, 4);
            pastedEdges.add(pastedEdge);
        }

        new AddNodesAndEdgesEdit(graph, pastedNodes, pastedEdges);
    }

    /**
     * @return Whether the Clipboard is currently storing any Nodes or Edges.
     *
     * After the first call to Clipboard.store() this will always be true for the duration of the program - given that
     * a non-empty list of Nodes or Edges is given to Clipboard.store().
     */
    public boolean hasContents() {
        return !storedNodes.isEmpty() || !storedEdges.isEmpty();
    }
}