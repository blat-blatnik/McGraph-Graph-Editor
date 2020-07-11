package controller.menuitems;

import model.Graph;
import utils.GraphIO;
import utils.KeyUtil;
import view.GraphPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * @author Boris
 * @version 1.1
 *
 * This control allows the user to select a new graph file to load from their system
 *
 * @see Graph
 * @see GraphIO
 * @see controller.MenuBar
 */
public class OpenMenuItem extends JMenuItem {

    /**
     * Constructs a OpenMenuItem for the specified graph and panel.
     *
     * @param graph The GraphModel that the new graph files will be loaded onto.
     * @param panel The GraphPanel whose view will be centered on the newly loaded graphs.
     */
    public OpenMenuItem(Graph graph, GraphPanel panel) {
        super("Open...");
        setToolTipText("Choose a graph to open.");

        addActionListener(e -> {
            String loadFile = GraphIO.chooseLoadFile(graph.getFilename());
            if (loadFile == null)
                return;
            if (new File(loadFile).exists()) {
                GraphIO.loadGraph(graph, loadFile);
                panel.centerViewOnGraph();
            } else {
                JOptionPane.showMessageDialog(null,
                        "The selected file does not exist.",
                        "Couldn't open file",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        setMnemonic(KeyEvent.VK_O);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyUtil.MENU_KEY_MASK));
    }

}