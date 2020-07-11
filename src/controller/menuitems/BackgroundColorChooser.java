package controller.menuitems;

import view.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author Boris
 * @version 1.0
 *
 * This control allows the user to set the background color of the GraphPanel.
 *
 * @see GraphPanel
 * @see controller.MenuBar
 */
public class BackgroundColorChooser extends JMenuItem {

    /**
     * Constructs a new BackGroundColorChooser for the specified panel.
     *
     * @param panel The GraphPanel whose background color will be changed.
     */
    public BackgroundColorChooser(GraphPanel panel) {
        super("Background Color...");
        setToolTipText("Select a new background color.");

        addActionListener(e -> {
            Color oldColor = panel.getBackground();
            Color newColor = JColorChooser.showDialog(null, "Choose a color", oldColor);
            panel.setBackground(newColor);
        });

        setMnemonic(KeyEvent.VK_B);
    }

}