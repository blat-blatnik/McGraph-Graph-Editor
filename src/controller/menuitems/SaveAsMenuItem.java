package controller.menuitems;

import model.Graph;
import utils.GraphIO;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * @author Boris
 * @version 1.0
 *
 * This control allows the user to save the current graph to a file of their choosing.
 *
 * @see Graph
 * @see GraphIO
 * @see controller.MenuBar
 */
public class SaveAsMenuItem extends JMenuItem {

    /**
     * Constructs a SaveAsMenuItem for the given graph.
     *
     * @param graph The GraphModel that will be saved to a new file.
     */
    public SaveAsMenuItem(Graph graph) {
        super("Save as...");
        setToolTipText("Save the current graph to a chosen file.");

        addActionListener(e -> GraphIO.saveGraph(graph, GraphIO.chooseSaveFile(graph.getFilename())));

        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyUtil.MENU_KEY_MASK | KeyEvent.SHIFT_DOWN_MASK));
    }
}