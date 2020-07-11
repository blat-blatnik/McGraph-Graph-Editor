package controller.toolbaritems;

import model.Graph;
import model.Node;
import controller.undoableedits.NodeEdit;
import utils.ListUtil;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * @author Boris
 * @version 2.1
 *
 * This control can be used to toggle some property of the fonts of all selected Nodes. The property to toggle is a
 * parameter of the constructor, so this one class can be used for any font property - although only ITALIC and BOLD
 * are actually used. When the user hovers their mouse over this button the fonts of all selected nodes will temporarily
 * have the appropriate property temporarily toggled in order to give some indication to the user of what their changes
 * will look like.
 *
 * @see Node
 * @see controller.NodeToolBar
 */
public class NodeFontPropertyToggle extends JToggleButton {

    private final Graph graph;
    private final int propertyToToggle;
    private boolean shouldChangeNodes;

    /**
     * Constructs a NodeFontPropertyToggle for the given graph, with the given text and tooltip text. The property
     * that this button should toggle is also provided.
     *
     * @param graph The GraphModel whose selected Nodes will have their font properties changed.
     * @param text The displayed on this button.
     * @param toolTipText The tooltip text that is shown when the used hovers their mouse over this button.
     * @param propertyToToggle The font property that this button will toggle on the Nodes.
     */
    public NodeFontPropertyToggle(Graph graph, String text, String toolTipText, int propertyToToggle) {
        super(text);
        setToolTipText(toolTipText);

        this.graph = graph;
        this.propertyToToggle = propertyToToggle;
        shouldChangeNodes = true;

        setProperties();
        graph.addObserver((o, msg) -> setProperties());

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!shouldChangeNodes)
                    return;

                boolean setProperty = !allNodeFontsHaveThisProperty();
                new NodeEdit(graph, graph.getSelectedNodes(), node -> {
                    Font oldFont = node.getActualFont();
                    int oldStyle = oldFont.getStyle();
                    if (setProperty)
                        node.setActualFont(oldFont.deriveFont(oldStyle | propertyToToggle));
                    else
                        node.setActualFont(oldFont.deriveFont(oldStyle & ~propertyToToggle));
                });
            }
        };

        addActionListener(action);
        if (propertyToToggle == Font.BOLD)
            KeyUtil.addPressAction(this, KeyEvent.VK_B, KeyUtil.MENU_KEY_MASK, action);
        else if (propertyToToggle == Font.ITALIC)
            KeyUtil.addPressAction(this, KeyEvent.VK_I, KeyUtil.MENU_KEY_MASK, action);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                for (Node node : graph.getSelectedNodes()) {
                    Font font = node.getActualFont();
                    node.setVisualFont(font.deriveFont(font.getStyle() | propertyToToggle));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                for (Node node : graph.getSelectedNodes())
                    node.setVisualFont(node.getActualFont());
            }
        });
    }

    /**
     * Changes the font of this button to match whether the property it toggles is enabled or disabled on the selected
     * Nodes. If no Nodes are selected the button will be made invisible.
     */
    private void setProperties() {
        List<Node> selectedNodes = graph.getSelectedNodes();

        if (selectedNodes.isEmpty())
            setVisible(false);
        else {
            setVisible(true);
            Font oldFont = getFont();
            int oldStyle = oldFont.getStyle();

            shouldChangeNodes = false;
            if (allNodeFontsHaveThisProperty()) {
                setSelected(true);
                setFont(oldFont.deriveFont(oldStyle | propertyToToggle));
            } else {
                setSelected(false);
                setFont(oldFont.deriveFont(oldStyle & ~propertyToToggle));
            }
            shouldChangeNodes = true;
        }
    }

    /**
     * @return Whether all selected Nodes' fonts have the property that this button is supposed to toggle.
     */
    private boolean allNodeFontsHaveThisProperty() {
        List<Node> selectedNodes = graph.getSelectedNodes();
        return ListUtil.all(selectedNodes, node -> (node.getActualFont().getStyle() & propertyToToggle) != 0);
    }

}