package controller.toolbaritems;

import controller.undoableedits.EdgeEdit;
import model.Edge;
import model.Graph;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @version 1.5
 *
 * This control allows the user to modify the weights of the selected Edges of a graph.
 *
 * @see Edge
 * @see controller.EdgeToolBar
 */
public class EdgeWeightChooser extends JSpinner {

    private final Graph graph;
    private boolean shouldChangeEdges;

    /**
     * Constructs a EdgeWeightChooser for the specified graph.
     *
     * @param graph The GraphModel whose selected Edges will have their weights changed.
     */
    public EdgeWeightChooser(Graph graph) {
        super(new SpinnerNumberModel(1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        setToolTipText("Change the weight of the selected edges.");
        setFont(Edge.DEFAULT_FONT.deriveFont(12.0f));

        this.graph = graph;
        shouldChangeEdges = true;

        setProperties();
        graph.addObserver((o, msg) -> setProperties());

        addChangeListener(e -> {
            if (!shouldChangeEdges)
                return;
            new EdgeEdit(graph, graph.getSelectedEdges(), edge -> edge.setActualWeight(getWeight()));
        });

        setFocusable(true);
        KeyUtil.addPressAction(this, KeyEvent.VK_F2, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)getEditor();
                JFormattedTextField textField = editor.getTextField();
                textField.requestFocus();
                textField.selectAll();
            }
        });
    }

    /**
     * Sets the value of this JSpinner to the weight of the first selected Edge. If there are no selected Edges then
     * make this JSpinner invisible.
     */
    private void setProperties() {
        List<Edge> selectedEdges = graph.getSelectedEdges();
        if (selectedEdges.isEmpty())
            setVisible(false);
        else {
            setVisible(true);
            Edge firstEdge = selectedEdges.get(0);
            if (getWeight() != firstEdge.getActualWeight()) {
                shouldChangeEdges = false;
                setValue(firstEdge.getActualWeight());
                shouldChangeEdges = true;
            }
        }
    }

    /**
     * @return The value of this JSpinner formatted as a double.
     */
    private double getWeight() {
        return (Double)getValue();
    }

}