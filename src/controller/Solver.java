package controller;

import controller.undoableedits.NodeAndEdgeEdit;
import controller.undoableedits.NodeEdit;
import model.Edge;
import model.Graph;
import model.Node;
import utils.Action;
import utils.TextUtil;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @version 2.2
 *
 * This class follows the Singleton Design pattern, meaning only one instance can be created of this class.
 *
 * This class contains methods for 'solving' the graphs of a GraphModel - finding the shortest path between any
 * 2 Nodes, as well as marking the distance of every Node to some starting Node. These functions also visually label
 * the Nodes in the GraphModel. For example, Nodes along the shortest path will be highlighted and other Nodes will be
 * dimmed.
 *
 * We want to be able to fully handle negative weights (including infinite negative cycles) so we use the Bellman-Ford
 * algorithm. Normally Bellman-Ford terminates with an error upon discovering an infinite negative cycle in the graph,
 * but we want to also be able to handle this case. What we currently do is find all nodes which can be reached from
 * the negative cycle with a depth-first search and mark them all as -INFINITY distance.
 *
 * @see Graph
 * @see Node
 * @see Edge
 */
public class Solver {

    /**
     * The Color used to mark the start Node for the solve operation.
     */
    public static final Color START_COLOR = new Color(25, 151, 203);

    /**
     * The Color used to mark the goal Node for the solve operation.
     */
    public static final Color GOAL_COLOR = new Color(168, 26, 34);

    private static final Color EXPLORED_COLOR = new Color(210, 135, 12);
    private static final Color UNEXPLORED_COLOR = new Color(98, 98, 98);
    private static final Color MARKED_NODE_TEXT_COLOR = new Color(0, 0, 0);

    /**
     * Gets the only instance of the Solver.
     * @return Solver instance
     */
    public static Solver getInstance(){
        return Solver.SingletonHelper.INSTANCE;
    }

    /**
     * Explores every Node in the given Graph and calculates it's distance from the currently marked start Node. If a
     * Node is not reachable from the start Node, then it's distance is set to +INFINITY. All Nodes are visually marked
     * with their respective distance.
     *
     * @param graph The GraphModel whose Nodes to explore and mark.
     * @see Graph
     * @see Node
     * @see Edge
     */
    public void exploreAndMarkWholeGraph(Graph graph) {

        Node startNode = graph.getStartNode();
        if (startNode == null)
            throw new IllegalStateException("Graph has no start node");

        int N = graph.getNodes().size();
        Map<Node, Double> dist = new HashMap<>(N);
        Map<Node, Edge> path = new HashMap<>(N);

        exploreWholeGraph(graph, startNode, dist, path);

        Action<Node> nodeAction = node -> {
            double distanceToStart = dist.get(node);
            String name = node.getActualName();
            if (name.length() == 0)
                name += TextUtil.format(distanceToStart);
            else
                name += " (" + TextUtil.format(distanceToStart) + ")";
            node.setActualName(name);

            Edge pathEdge = path.getOrDefault(node, null);
            if (pathEdge != null) {
                node.setActualFillColor(EXPLORED_COLOR);
                node.setActualBorderColor(EXPLORED_COLOR.darker());
            } else if (node == startNode) {
                node.setActualFillColor(START_COLOR);
                node.setActualBorderColor(START_COLOR.darker());
            } else {
                node.setActualFillColor(UNEXPLORED_COLOR);
                node.setActualBorderColor(UNEXPLORED_COLOR.darker());
            }

            node.setActualTextColor(MARKED_NODE_TEXT_COLOR);
        };

        Action<Edge> edgeAction = edge -> {
            if (path.containsValue(edge))
                edge.setActualColor(EXPLORED_COLOR);
            else
                edge.setActualColor(UNEXPLORED_COLOR);
        };

        new NodeAndEdgeEdit(graph, graph.getNodes(), graph.getEdges(), nodeAction, edgeAction);
    }

    /**
     * Finds the shortest path between the Nodes marked as the start and end nodes in a given GraphModel. If a valid
     * path is found, then the Nodes and Edges along the path are highlighted and the Nodes and Edges not on the path
     * are dimmed accordingly. If the goal Node is not reachable from the start Node, then a popup message appears to
     * explain this to the user.
     *
     * @param graph The GraphModel whose start and goal Nodes to find the shortest paths between.
     * @see Graph
     * @see Node
     * @see Edge
     */
    public void markShortestPath(Graph graph) {

        Node startNode = graph.getStartNode();
        Node goalNode = graph.getGoalNode();
        if (startNode == null)
            throw new IllegalStateException("Graph has no start node");
        if (goalNode == null)
            throw new IllegalStateException("Graph has no goal node");

        List<Edge> path = findShortestPath(graph, startNode, goalNode);

        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "There is no path between the marked start and goal nodes.",
                    "No Path Found", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Action<Node> nodeAction = node -> {
                if (node == startNode)
                    node.setActualFillColor(START_COLOR);
                else if (node == goalNode) {
                    node.setActualFillColor(GOAL_COLOR);
                } else {

                    boolean isPartOfPath = false;
                    for (Edge edge : path) {
                        if (edge.connectsTo(node)) {
                            isPartOfPath = true;
                            break;
                        }
                    }

                    if (isPartOfPath)
                        node.setActualFillColor(EXPLORED_COLOR);
                    else
                        node.setActualFillColor(UNEXPLORED_COLOR);
                }

                node.setActualBorderColor(node.getActualFillColor().darker());
                node.setActualTextColor(MARKED_NODE_TEXT_COLOR);
            };

            Action<Edge> edgeAction = edge -> {
                if (path.contains(edge))
                    edge.setActualColor(EXPLORED_COLOR);
                else
                    edge.setActualColor(UNEXPLORED_COLOR);
            };

            new NodeAndEdgeEdit(graph, graph.getNodes(), graph.getEdges(), nodeAction, edgeAction);
        }

    }

    /**
     * Uses a greedy coloring algorithm to order to color the Nodes in the given GraphModel such that no 2 Nodes that
     * are connected by an Edge have the same Color. This greedy algorithm will NOT find the optimal color - that is
     * it will not use the fewest distinct colors possible to color the graph. However, it runs very quickly and gives
     * reasonably good results. It used the degeneracy ordering strategy that wil produce close to optimal colorings in
     * most cases.
     *
     * @param graph The GraphModel whose Nodes to color.
     */
    public void colorGraph(Graph graph) {
        List<Node> graphNodes = graph.getNodes();
        if (graphNodes.isEmpty())
            return;

        List<Node> nodes = new ArrayList<>(graphNodes.size());
        List<Edge> edges = graph.getEdges();
        Map<Node, Integer> colorMap = new HashMap<>(nodes.size());
        int highestColorIndex = -1;

        //NOTE(Boris): Use the degeneracy ordering strategy as a heuristic - this will produce close to optimal
        // colorings in most cases.
        Set<Node> nodesToSort = new HashSet<>(graphNodes);
        while (!nodesToSort.isEmpty()) {
            Node minDegreeNode = null;
            int minDegree = Integer.MAX_VALUE;
            for (Node node : nodesToSort) {
                int degree = 0;
                for (Edge edge : edges) {
                    if (edge.connectsTo(node) && nodesToSort.contains(edge.getOtherNode(node)))
                        ++degree;
                }
                if (minDegree >= degree) {
                    minDegree = degree;
                    minDegreeNode = node;
                }
            }
            nodes.add(minDegreeNode);
            nodesToSort.remove(minDegreeNode);
        }
        Collections.reverse(nodes);

        for (Node node : nodes) {

            Set<Integer> usedNeighborColors = new HashSet<>();

            for (Edge edge : edges) {
                if (edge.connectsTo(node)) {
                    Node neighbor = edge.getOtherNode(node);
                    if (colorMap.containsKey(neighbor))
                        usedNeighborColors.add(colorMap.get(neighbor));
                }
            }

            int colorIndex = 0;
            while (usedNeighborColors.contains(colorIndex))
                ++colorIndex;
            colorMap.put(node, colorIndex);

            if (highestColorIndex < colorIndex)
                highestColorIndex = colorIndex;
        }

        int numColors = 1 + highestColorIndex;

        new NodeEdit(graph, graph.getNodes(), node -> {
            float hue = colorMap.get(node) / (float)(numColors);
            Color color = Color.getHSBColor(hue, 0.5f, 0.8f);
            node.setActualFillColor(color);
            node.setActualBorderColor(color.darker());
            node.setActualTextColor(MARKED_NODE_TEXT_COLOR);
        });
    }

    /**
     * Uses the Bellman-Ford algorithm to find the shortest path between the given start and goal Nodes in the given
     * GraphModel.
     *
     * @param graph The GraphModel in which to search.
     * @param start The Node from which the path should start.
     * @param goal The Node on which the path should end - if possible.
     * @return A sequence of Edges connecting the start and goal Nodes that minimizes the sum of Edge weights - or an empty List if the goal Node is not reachable, or null if negative cycles were found.
     * @see Graph
     * @see Node
     * @see Edge
     */
    private List<Edge> findShortestPath(Graph graph, Node start, Node goal) {
        int N = graph.getNodes().size();
        Map<Node, Double> dist = new HashMap<>(N);
        Map<Node, Edge> edgePath = new HashMap<>(N);

        exploreWholeGraph(graph, start, dist, edgePath);

        List<Edge> path = new ArrayList<>();

        Node u = goal;
        Edge edge = edgePath.getOrDefault(u, null);

        //NOTE(Boris): We need to keep track of which Edges we visited - otherwise we would get into trouble with
        // infinite negative cycles.
        Set<Edge> visitedEdges = new HashSet<>();

        while (edge != null && !visitedEdges.contains(edge)) {
            visitedEdges.add(edge);
            u = edge.getOtherNode(u);
            path.add(edge);
            edge = edgePath.get(u);
        }

        return path;
    }

    /**
     * Uses the Bellman-Ford algorithm to explore every Node in the given GraphModel and find it's shortest distance to
     * the given starting Node. The results of this method are stored in the Maps that are passed in as parameters.
     *
     * @param graph The GraphModel to explore - can have cycles, negative edge weights, or even negative cycles.
     * @param start The Node from which the search should start.
     * @param outDist An empty map into which this method will store the distance of every Node in the given GraphModel to the start Node, or +INFINITY if the Node is not reachable.
     * @param outPath An empty map into which this method will store for every Node in the GraphModel the Edge taken to first reach that Node, or null if the Node is not reachable.
     *
     * @see Graph
     * @see Node
     * @see Edge
     */
    private void exploreWholeGraph(
            Graph graph,
            Node start,
            Map<Node, Double> outDist,
            Map<Node, Edge> outPath)
    {

        List<Node> nodes = graph.getNodes();
        List<Edge> edges = graph.getEdges();

        outDist.clear();
        outPath.clear();

        for (Node node : nodes) {
            outPath.put(node, null);
            if (node == start)
                outDist.put(node, 0.0);
            else
                outDist.put(node, Double.POSITIVE_INFINITY);
        }

        int numIterationsLeft = nodes.size() - 1;
        boolean updatedDuringLastIteration = true;

        while (numIterationsLeft > 0 && updatedDuringLastIteration) {

            --numIterationsLeft;
            updatedDuringLastIteration = false;

            for (Edge edge : edges) {
                Node node1 = edge.getNode1();
                Node node2 = edge.getNode2();
                double weight = edge.getActualWeight();
                double dist1 = outDist.get(node1);
                double dist2 = outDist.get(node2);
                double newDist1 = dist2 + weight;
                double newDist2 = dist1 + weight;

                if (edge.isDirectedTo(node1) && newDist1 < dist1) {
                    outDist.replace(node1, newDist1);
                    outPath.replace(node1, edge);
                    updatedDuringLastIteration = true;
                }
                if (edge.isDirectedTo(node2) && newDist2 < dist2) {
                    outDist.replace(node2, newDist2);
                    outPath.replace(node2, edge);
                    updatedDuringLastIteration = true;
                }
            }
        }

        //NOTE(Boris): Check for negative cycles here.
        for (Edge edge : edges) {
            Node node1 = edge.getNode1();
            Node node2 = edge.getNode2();
            double weight = edge.getActualWeight();
            double dist1 = outDist.get(node1);
            double dist2 = outDist.get(node2);
            double newDist1 = dist2 + weight;
            double newDist2 = dist1 + weight;

            //NOTE(Boris): Any node that can reach these nodes can have -INFINITY distance by going through the cycle
            // over and over - so just search for all reachable Nodes and set them to -INFINITY distance.
            if (edge.isDirectedTo(node1) && newDist1 < dist1)
                visitReachableNodes(graph, node1, node -> outDist.replace(node, Double.NEGATIVE_INFINITY));
            if (edge.isDirectedTo(node2) && newDist2 < dist2)
                visitReachableNodes(graph, node2, node -> outDist.replace(node, Double.NEGATIVE_INFINITY));
        }
    }

    /**
     * Visits all Nodes in a GraphModel that are reachable from a given start Node and performs a given NodeAction on
     * them. The graph is explored using a depth-first-search.
     *
     * @param graph The GraphModel to search through.
     * @param start The Node from which to start search the search.
     * @param visitedNodeAction The NodeAction to perform on all Nodes reachable from the start Node *including* the start Node itself.
     *
     * @see Graph
     * @see Node
     * @see Edge
     * @see Action
     */
    private void visitReachableNodes(Graph graph, Node start, Action<Node> visitedNodeAction) {
        Set<Node> visitedSet = new HashSet<>();
        Stack<Node> stack = new Stack<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            if (!visitedSet.contains(node)) {
                visitedSet.add(node);
                visitedNodeAction.doAction(node);
                for (Edge edge : graph.getEdges()) {
                    if (edge.connectsTo(node)) {
                        Node otherNode = edge.getOtherNode(node);
                        if (edge.isDirectedTo(otherNode))
                            stack.push(otherNode);
                    }
                }
            }
        }
    }

    /**
     * This class follows the Singleton pattern and should only be instantiated once,
     * therefore the constructor is private, so it cannot be instantiated outside of this class.
     */
    private Solver() {}

    /**
     * This way of creating a Singleton class is thread-safe and ensures lazy loading.
     * The instance is only loaded when accessed through the getInstance() method.
     */
    private static class SingletonHelper {
        private static final Solver INSTANCE = new Solver();
    }
}