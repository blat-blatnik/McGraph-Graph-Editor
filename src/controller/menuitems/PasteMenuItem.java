package controller.menuitems;

import model.Graph;
import controller.Clipboard;
import utils.KeyUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * @author Boris
 * @author Jana
 * @version 1.1
 *
 * This control allows the user to paste Nodes and Edges previously saved to the Clipboard.
 *
 * @see Graph
 * @see Clipboard
 * @see controller.MenuBar
 */
public class PasteMenuItem extends JMenuItem {

    private final Clipboard clipboard;

    /**
     * Constructs a PasteMenuItem for the given graph.
     *
     * @param graph The GraphModel where the Nodes and Edges will be pasted.
     */
    public PasteMenuItem(Graph graph) {
        super("Paste");
        setToolTipText("Paste the nodes from the clipboard.");

        this.clipboard = Clipboard.getInstance();
        setProperties();

        clipboard.addObserver((obj, msg) -> setProperties());
        addActionListener(e -> clipboard.paste(graph));

        setMnemonic(KeyEvent.VK_P);
        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyUtil.MENU_KEY_MASK));
    }

    /**
     * Enables or disables this button based on whether the Clipboard has any contents to paste.
     */
    private void setProperties() {
        setEnabled(clipboard.hasContents());
    }

}