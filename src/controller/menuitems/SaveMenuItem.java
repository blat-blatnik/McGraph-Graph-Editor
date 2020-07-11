package controller.menuitems;

import model.Graph;
import utils.GraphIO;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
* @version 1.2
 *
 * This control allows the user to save the current graph back to the file it was loaded from - or to a new file
 * if the graph was made without loading from an old file.
 *
 * @see Graph
 * @see GraphIO
 * @see controller.MenuBar
 */
public class SaveMenuItem extends JMenuItem {

    /**
     * Constructs a SaveMenuItem for the given graph.
     *
     * @param graph The GraphModel that will be saved.
     */
    public SaveMenuItem(Graph graph) {
        super("Save");
        setToolTipText("Save the current graph file.");

        addActionListener(e -> {
            String saveFile = graph.getFilename();
            if (saveFile == null || saveFile.length() == 0)
                saveFile = GraphIO.chooseSaveFile(graph.getFilename());
            GraphIO.saveGraph(graph, saveFile);
        });

        setMnemonic(KeyEvent.VK_S);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyUtil.MENU_KEY_MASK));
    }

}