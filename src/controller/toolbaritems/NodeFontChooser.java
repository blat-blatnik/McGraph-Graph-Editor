package controller.toolbaritems;

import model.Graph;
import model.Node;
import controller.animation.Animation;
import controller.animation.ColorBlinkAnimation;
import controller.undoableedits.NodeEdit;
import utils.TextUtil;
import utils.ListUtil;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author Boris
 * @version 2.2
 *
 * This control allows the user to select a font for all selected Nodes from a drop down menu. All entries in the menu
 * have their text shown using the font that they represent in order to make it easier for the user to choose which font
 * to go with. When the user highlights a font from the list, the Node text will be temporarily rendered in the font
 * so the user can preview their changes. All system fonts will be available to choose from.
 *
 * @see Node
 * @see controller.NodeToolBar
 * @see ColorBlinkAnimation
 */
public class NodeFontChooser extends JComboBox<String> {

    private final Graph graph;
    private boolean shouldChangeNodes;
    private final Animation textColorAnimation;

    /**
     * Constructs a NodeFontChooser for a given graph.
     *
     * @param graph The GraphModel whose selected Nodes will have their fonts altered.
     */
    public NodeFontChooser(Graph graph) {
        super(TextUtil.ALL_FONT_NAMES);
        setToolTipText("Select a font for the text of the selected nodes.");
        setSelectedIndex(0);

        this.graph = graph;
        textColorAnimation = new ColorBlinkAnimation<>(
                graph::getSelectedNodes, Node::getActualTextColor, Node::setVisualTextColor);
        shouldChangeNodes = true;

        //NOTE(Boris): The code below makes this combo box actually load at a reasonable speed.
        // Without it, loading the box takes like 2 seconds and feels very unresponsive.
        // Adapted from: https://stackoverflow.com/a/5896414
        setRenderer(new Renderer());
        setPrototypeDisplayValue(ListUtil.getLongest(TextUtil.ALL_FONT_NAMES));
        ComboPopup popup = (ComboPopup)getUI().getAccessibleChild(this, 0);
        @SuppressWarnings("unchecked")
        JList<String> popupList = popup.getList();
        popupList.setPrototypeCellValue(getPrototypeDisplayValue());

        setProperties();
        graph.addObserver((o, msg) -> setProperties());

        addActionListener(e -> {
            if (!shouldChangeNodes)
                return;

            String newFontName = TextUtil.ALL_FONT_NAMES[getSelectedIndex()];

            textColorAnimation.stop();
            textColorAnimation.setCurrentTime(0);
            for (Node node : graph.getSelectedNodes())
                node.setVisualTextColor(node.getActualTextColor());

            new NodeEdit(graph, graph.getSelectedNodes(), node -> {
                Font oldFont = node.getActualFont();
                Font newFont = new Font(newFontName, oldFont.getStyle(), oldFont.getSize());
                node.setActualFont(newFont);
            });
        });

        MouseListener hoverListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                textColorAnimation.play();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                textColorAnimation.stop();
                textColorAnimation.setCurrentTime(0);
                for (Node node : graph.getSelectedNodes())
                    node.setVisualTextColor(node.getActualTextColor());
            }
        };

        //NOTE(Boris): Whenever the use hovers the mouse over one of the fonts in the popup list, the visual font
        // of the selected nodes gets set to the font that is hovered over in the list. When the menu closes - this
        // should be changed back.
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                textColorAnimation.stop();
                textColorAnimation.setCurrentTime(0);
                for (Node node : graph.getSelectedNodes())
                    node.setVisualFont(node.getActualFont());
                for (Node node : graph.getSelectedNodes())
                    node.setVisualTextColor(node.getActualTextColor());
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
        });

        addMouseListener(hoverListener);
        popupList.addMouseListener(hoverListener);
    }

    /**
     * Sets the selected item in the drop-down list to be the same as the font of the currently selected Node. If no
     * Nodes are selected, then makes this button invisible.
     */
    private void setProperties() {
        List<Node> selectedNodes = graph.getSelectedNodes();

        if (selectedNodes.isEmpty())
            setVisible(false);
        else {
            setVisible(true);
            Node firstNode = selectedNodes.get(0);
            String fontName = firstNode.getActualFont().getName();
            int index = Arrays.binarySearch(TextUtil.ALL_FONT_NAMES, fontName);
            if (index != getSelectedIndex()) {
                shouldChangeNodes = false;
                setSelectedIndex(index);
                shouldChangeNodes = true;
            }
        }
    }

    /**
     * This custom Renderer is used to display the list cell components.
     *
     * We need a special renderer for 2 things: First, we want to set the font of every item in the list individually.
     * Second, we want to set the visual font of selected node when the user hovers over one of the list cells.
     */
    private class Renderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component label = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font(value.toString(), Font.PLAIN,14));

            //NOTE(Boris): Apparently isSelected says whether this particular list entry is hovered over by the mouse.
            if (isSelected) {
                for (Node node : graph.getSelectedNodes()) {
                    Font oldFont = node.getActualFont();
                    Font newFont = new Font(value.toString(), oldFont.getStyle(), oldFont.getSize());
                    node.setVisualFont(newFont);
                }
            }

            return label;
        }
    }

}