package controller;

import controller.undoableedits.EdgeEdit;
import model.*;
import controller.animation.NodeSizeAnimation;
import controller.undoableedits.AddNodesAndEdgesEdit;
import controller.undoableedits.NodeEdit;
import utils.MathUtil;
import utils.KeyUtil;
import view.GraphPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Boris
 * @author Jana
 * @version 999.0
 *
 * This controller class is responsible for handling most mouse related events such as mouse pressed, mouse released,
 * mouse dragged, etc.
 *
 * @see Graph
 * @see GraphPanel
 */
public class SelectionController extends MouseAdapter {

    private static final double THRESHOLD_DISTANCE_TO_START_DRAGGING = 6.0;

    private final Graph graph;
    private final GraphPanel panel;
    private Point2D lastMousePos;
    private Point2D dragStartPos;
    private boolean isDragging;
    private boolean isChangingNode;
    private boolean isChangingEdge;
    private final List<Node> movableNodesStart;
    private final List<Edge> movableEdgesStart;

    /**
     * Constructs a new SelectionController for the given GraphModel and the given GraphPanel.
     *
     * @param graph The GraphModel that this SelectionController should control.
     * @param panel The GraphPanel that this SelectionController should control.
     */
    public SelectionController(Graph graph, GraphPanel panel) {
        super();
        this.graph = graph;
        this.panel = panel;
        lastMousePos = null;
        dragStartPos = null;
        isDragging = false;
        isChangingNode = false;
        isChangingEdge = false;
        movableNodesStart = new ArrayList<>();
        movableEdgesStart = new ArrayList<>();
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
        panel.addMouseWheelListener(this);
    }

    /**
     * This method fires when a mouse button is pressed while the GraphPanel is in focus. It handles selection of
     * Nodes and Edges, including multi-selection.
     *
     * @param event The MouseEvent arguments that specify which event occurred.
     */
    @Override
    public void mousePressed(MouseEvent event) {
        Point2D graphPoint = panel.projectToGraphSpace(event.getPoint());
        Node selectedNode = getNodeAt(graphPoint);

        boolean controlIsDown = KeyUtil.isMenuModifierDown(event);

        // If a user is adding an edge and presses the mouse again, the edge is either connect to
        // the selected node and added as an edit to undo manager, or the edge is discarded if no
        // node is selected.
        if (graph.isAddingEdges()) {

            List<Edge> newEdges = new ArrayList<>(graph.getUnfinishedEdges().size());

            for (Edge unfinishedEdge : graph.getUnfinishedEdges()) {
                Node otherNode = unfinishedEdge.getNode1();
                if (selectedNode != null && selectedNode != otherNode) {
                    Edge newEdge = new Edge(otherNode, selectedNode);
                    newEdges.add(newEdge);
                }
            }

            if (!newEdges.isEmpty())
                new AddNodesAndEdgesEdit(graph, null, newEdges);

            graph.stopAddingEdges();
        }

        //NOTE(Boris): Edges are only selected if no nodes were selected.
        if (selectedNode == null) {
            Edge selectedEdge = getEdgeAt(graphPoint);
            boolean isSelected = graph.isSelected(selectedEdge);
            if (!controlIsDown) {
                if (!isSelected) {
                    graph.clearSelectedNodes();
                    graph.clearSelectedEdges();
                    graph.select(selectedEdge);
                }
            } else { // controlIsDown
                if (isSelected)
                    graph.deselect(selectedEdge);
                else
                    graph.select(selectedEdge);
            }

        } else {
            graph.setNodeClicked(true);

            NodeBorders selectedBorder = getNodeBorderAt(selectedNode, graphPoint);
            //NOTE(Boris): Need to check if node is selected *before* clearing all selected nodes.
            boolean isSelected = graph.isSelected(selectedNode);
            if (!controlIsDown) {
                if (!isSelected) {
                    graph.clearSelectedEdges();
                    graph.clearSelectedNodes();
                    graph.select(selectedNode);
                }
                for (Node node : graph.getSelectedNodes())
                    node.setSelectedBorders(selectedBorder);

                if (selectedBorder == NodeBorders.NONE)
                    new NodeSizeAnimation(selectedNode, 8, 8, 0.20).play();
            } else { // controlIsDown
                if (isSelected) {
                    graph.deselect(selectedNode);
                    for (Node node : graph.getSelectedNodes())
                        node.setSelectedBorders(NodeBorders.NONE);
                } else {
                    graph.select(selectedNode);
                    for (Node node : graph.getSelectedNodes())
                        node.setSelectedBorders(selectedBorder);
                }
            }
        }

        lastMousePos = event.getPoint();
        dragStartPos = event.getPoint();


        // Copying the nodes that are moved to store old position for undo.
        for (Node node: graph.getSelectedNodes()){
            Node copy = new Node(node);
            movableNodesStart.add(copy);
        }

        // Copying the edges that are moved to store old position for undo.
        for (Edge edge: graph.getSelectedEdges()){
            Edge copy = new Edge(edge);
            movableEdgesStart.add(copy);
        }
    }

    /**
     * This method fires when a mouse button that is pressed down is released over the GraphPanel. This method mostly
     * finalizes any dragging operation that occurred before.
     *
     * @param event The MouseEvent arguments that specify which event occurred.
     */
    @Override
    public void mouseReleased(MouseEvent event) {
        if (isDragging) {
            // Reset selection rectangle
            panel.setSelectionRectangle(null);

            boolean controlIsDown = KeyUtil.isMenuModifierDown(event);

            // Finalizing a moving node with undoable edit
            if (isChangingNode && !controlIsDown) {
                new NodeEdit(graph, graph.getSelectedNodes(), movableNodesStart,
                        node -> node.setActualBounds(node.getActualBounds()));
            }

            // Calculate movements
            Point2D newMousePos = event.getPoint();

            double deltaX = newMousePos.getX() - lastMousePos.getX();
            double deltaY = newMousePos.getY() - lastMousePos.getY();

            double scale = panel.getScale();
            double dX = deltaX / scale;
            double dY = deltaY / scale;

            // Finalizing a moving edge with undoable edit
            if (isChangingEdge && !controlIsDown){
                new EdgeEdit(graph, graph.getSelectedEdges(), movableEdgesStart,
                        edge -> edge.moveActualWeightPointBy(dX,dY));
            }
        }

        lastMousePos = null;
        dragStartPos = null;
        movableNodesStart.clear();
        movableEdgesStart.clear();
        isDragging = false;
        isChangingNode = false;
        isChangingEdge = false;
    }

    /**
     * This method is called when the user clicks a mouse button over the GraphPanel. It is only responsible for
     * creating new Nodes on a double-click.
     *
     * @param event The MouseEvent arguments that specify which event occurred.
     */
    @Override
    public void mouseClicked(MouseEvent event) {

        if (event.getButton() != MouseEvent.BUTTON1)
            return;

        boolean isModifierDown = event.isAltDown() || event.isControlDown() || event.isShiftDown() || event.isMetaDown();

        if (event.getClickCount() == 2 && !isModifierDown) {
            Point2D graphPoint = panel.projectToGraphSpace(event.getPoint());

            Node selectedNode = getNodeAt(graphPoint);
            if (selectedNode != null && graph.isSelected(selectedNode))
                return;

            Edge selectedEdge = getEdgeAt(graphPoint);
            if (selectedEdge != null && graph.isSelected(selectedEdge))
                return;

            Node newNode = new Node("New Node", graphPoint.getX(), graphPoint.getY());
            new AddNodesAndEdgesEdit(graph, Arrays.asList(newNode), null);
        }
    }

    /**
     * This method is called when the user moves the mouse cursor over the GraphPanel. It is responsible for
     * highlighting Nodes and Edges that the mouse cursor hovers over.
     *
     * @param event The MouseEvent arguments that specify which event occurred.
     */
    @Override
    public void mouseMoved(MouseEvent event) {

        boolean isControlDown = KeyUtil.isMenuModifierDown(event);

        if (graph.isAddingEdges()) {
            Point2D graphPoint = panel.projectToGraphSpace(event.getPoint());
            for (Edge unfinishedEdge : graph.getUnfinishedEdges()) {
                unfinishedEdge.getNode2().moveTo(
                        graphPoint.getX(),
                        graphPoint.getY());
            }
        }

        Point2D graphPoint = panel.projectToGraphSpace(event.getPoint());
        Node hoveredNode = getNodeAt(graphPoint);
        graph.setHoveredNode(hoveredNode);
        if (hoveredNode == null) {
            Edge hoveredEdge = getEdgeAt(graphPoint);
            graph.setHoveredEdge(hoveredEdge);
            if (hoveredEdge != null)
                panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            else
                panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            graph.setHoveredEdge(null);
            if (!graph.isAddingEdges() && !isControlDown) {
                NodeBorders hoveredNodeBorders = getNodeBorderAt(hoveredNode, graphPoint);
                switch (hoveredNodeBorders) {
                    case NONE:
                        panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                        break;
                    case LEFT:
                        panel.setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
                        break;
                    case RIGHT:
                        panel.setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
                        break;
                    case TOP:
                        panel.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
                        break;
                    case BOTTOM:
                        panel.setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
                        break;
                    case TOP_LEFT:
                        panel.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
                        break;
                    case TOP_RIGHT:
                        panel.setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
                        break;
                    case BOTTOM_LEFT:
                        panel.setCursor(new Cursor(Cursor.SW_RESIZE_CURSOR));
                        break;
                    case BOTTOM_RIGHT:
                        panel.setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
                        break;
                }
            }
        }
    }

    /**
     * This method is called when the user holds down a mouse button while moving the mouse cursor over the GraphPanel.
     * If any Nodes were selected, their position will be dragged. If any Edges are selected, their weight points will
     * be dragged. If the menu key is being held down, this method could start and move a selection rectangle that
     * can be used to select multiple Nodes and Edges.
     *
     * @param event The MouseEvent arguments that specify which event occurred.
     */
    @Override
    public void mouseDragged(MouseEvent event) {
        if (dragStartPos == null)
            return;

        Point2D newMousePos = event.getPoint();

        if (!isDragging) {
            double distanceToStart = Point2D.distance(newMousePos.getX(), newMousePos.getY(), dragStartPos.getX(), dragStartPos.getY());
            if (distanceToStart > THRESHOLD_DISTANCE_TO_START_DRAGGING)
                isDragging = true;
        }

        if (!isDragging)
            return;

        boolean isControlDown = KeyUtil.isMenuModifierDown(event);

        if (isControlDown) {

            Point2D startPos = panel.projectToGraphSpace(dragStartPos);
            Rectangle2D.Double selectionRect = new Rectangle2D.Double();
            selectionRect.setRect(startPos.getX(), startPos.getY(), 0, 0);
            selectionRect.add(panel.projectToGraphSpace(newMousePos));
            panel.setSelectionRectangle(selectionRect);

            for (Node node : graph.getNodes()) {
                if (selectionRect.contains(node.getActualBounds()) ) {
                    if (!graph.isSelected(node)) {
                        graph.select(node);
                        new NodeSizeAnimation(node, 8, 8, 0.20).play();
                    }
                }
                else
                    graph.deselect(node);
            }

            for (Edge edge : graph.getEdges()) {
                //NOTE(Boris): We can't just do selectionRect.contains(edge.getPath().getBounds()) because the path2D
                // bounds include all control points of bezier curves for some reason .. so this wouldn't be accurate
                // and we need to do it ourselves - ugh.
                if (MathUtil.contains(selectionRect, edge.getPath(), 1))
                    graph.select(edge);
                else
                    graph.deselect(edge);
            }

        } else { // !isControlDown

            double deltaX = newMousePos.getX() - lastMousePos.getX();
            double deltaY = newMousePos.getY() - lastMousePos.getY();

            if (graph.numSelectedNodes() == 0 && graph.numSelectedEdges() == 0)
                panel.translate(deltaX, deltaY);
            else {
                double scale = panel.getScale();
                double dX = deltaX / scale;
                double dY = deltaY / scale;

                if (graph.getHoveredNode() != null) {
                    graph.moveSelectedNodesBy(dX, dY);
                    isChangingNode = true;
                }
                if (graph.getHoveredEdge() != null) {
                    graph.moveSelectedEdgesBy(dX, dY);
                    isChangingEdge = true;
                }
            }
        }

        lastMousePos = newMousePos;
    }


    /**
     * This callback is fired when the mouse wheel is scrolled. It zooms the panel view in or out.
     *
     * @param event The MouseWheelEvent arguments for this event.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent event) {
        int rotation = -event.getWheelRotation();
        if (rotation > 0)
            panel.zoom(1.1, event.getX(), event.getY());
        else if (rotation < 0)
            panel.zoom(1.0 / 1.1, event.getX(), event.getY());
    }

    /**
     * @param position The position in graph space from which to get the Node from.
     * @return A Node approximately around the given position, or null if no Node is close to the position.
     */
    private Node getNodeAt(Point2D position) {
        List<Node> nodes = graph.getNodes();

        Rectangle2D selectionRect = MathUtil.centeredRectangle(position, 5, 5);

        //NOTE(Boris): We are looping in reverse here because nodes further in the list are drawn *on top of* nodes that
        // come before - so we have to do it in reverse here in order to keep consistent, you should be selecting the
        // node that is on top of any other node.
        for (int i = nodes.size() - 1; i >= 0; --i) {
            Node node = nodes.get(i);
            Shape shape = node.getDrawableShape();
            if (shape.intersects(selectionRect))
                return node;
        }

        return null;
    }

    /**
     * @param node The Node whose NodeBorders to get.
     * @param position The position in graph space from which to get the NodeBorders from.
     * @return The NodeBorders of the given Node approximately closest to the given position, or NodeBorders.NONE if no border is close to the point.
     */
    private NodeBorders getNodeBorderAt(Node node, Point2D position) {

        Rectangle2D bounds = node.getVisualBounds();
        Line2D left   = MathUtil.leftEdge(bounds);
        Line2D right  = MathUtil.rightEdge(bounds);
        Line2D top    = MathUtil.topEdge(bounds);
        Line2D bottom = MathUtil.bottomEdge(bounds);

        final double CUTOFF = 4.0; //NOTE(Boris): Node edges closer than 4 pixels to the point are selected.
        boolean isLeft   = MathUtil.distanceBetween(position, left)   < CUTOFF;
        boolean isRight  = MathUtil.distanceBetween(position, right)  < CUTOFF;
        boolean isTop    = MathUtil.distanceBetween(position, top)    < CUTOFF;
        boolean isBottom = MathUtil.distanceBetween(position, bottom) < CUTOFF;

        if (isTop && isRight)
            return NodeBorders.TOP_RIGHT;
        else if (isTop && isLeft)
            return NodeBorders.TOP_LEFT;
        else if (isBottom && isRight)
            return NodeBorders.BOTTOM_RIGHT;
        else if (isBottom && isLeft)
            return NodeBorders.BOTTOM_LEFT;
        else if (isTop)
            return NodeBorders.TOP;
        else if (isRight)
            return NodeBorders.RIGHT;
        else if (isBottom)
            return NodeBorders.BOTTOM;
        else if (isLeft)
            return NodeBorders.LEFT;
        else
            return NodeBorders.NONE;
    }

    /**
     * @param position The position in graph space at which to get the Edge at.
     * @return An Edge approximately around the given position, or null if no Edge is close.
     */
    private Edge getEdgeAt(Point2D position) {
        List<Edge> edges = graph.getEdges();
        Rectangle2D selectionRect = MathUtil.centeredRectangle(position, 5, 5);

        //NOTE(Boris): Just like with getNodeAt(), we should loop in reverse here.
        for (int i = edges.size() - 1; i >= 0; --i) {
            Edge edge = edges.get(i);
            Path2D edgePath = edge.getPath();
            //NOTE(Boris): We can't just use edgePath.intersects(selectionRect) because for some reason Path2D.intersects
            // tests the entire INTERIOR of the path, not just the path itself.. ugh..
            if (MathUtil.intersects(edgePath, selectionRect, 1))
                return edge;
        }

        return null;
    }

}