package controller;

import model.Graph;
import utils.KeyUtil;
import view.GraphFrame;
import view.GraphPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
* @version 2.0
 *
 * The Keyboard controller of the GraphPanel and GraphModel.
 *
 * @see Graph
 * @see GraphPanel
 */
public class KeyboardController {

    private final Graph graph;
    private final GraphPanel panel;

    /**
     * Constructs a new KeyboardController from the given GraphModel, GraphPanel, and GraphFrame. Also sets all the
     * necessary key bindings on the GraphPanel.
     *
     * @param graph The GraphModel that this KeyboardController should control.
     * @param panel The GraphPanel that this KeyboardController belongs to.
     * @param frame The GraphFrame that this KeyboardController should control.
     */
    public KeyboardController(Graph graph, GraphPanel panel, GraphFrame frame) {
        this.graph = graph;
        this.panel = panel;

        //NOTE(Boris): Ugh I hate these stupid input maps! Who thought this was a good idea?!
        // Sadly normal key listeners don't work - when you select a combo box, or something like that the panel
        // loses focus, and so it doesn't get any key events anymore even if it's the parent of the control that
        // has the focus.. what a shitty API design decision..

        KeyUtil.addPressAction(panel, KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (graph.numSelectedNodes() > 0 || graph.numSelectedEdges() > 0 || graph.isAddingEdges()) {
                    graph.clearSelectedNodes();
                    graph.clearSelectedEdges();
                    graph.stopAddingEdges();
                } else
                    frame.close();
            }
        });

        KeyUtil.addPressAction(panel, KeyEvent.VK_LEFT, 0, new ArrowKeyAction(KeyEvent.VK_LEFT));
        KeyUtil.addPressAction(panel, KeyEvent.VK_RIGHT, 0, new ArrowKeyAction(KeyEvent.VK_RIGHT));
        KeyUtil.addPressAction(panel, KeyEvent.VK_UP, 0, new ArrowKeyAction(KeyEvent.VK_UP));
        KeyUtil.addPressAction(panel, KeyEvent.VK_DOWN, 0, new ArrowKeyAction(KeyEvent.VK_DOWN));
    }

    /**
     * Represents an action that is performed when an arrow key is pressed - since all of these do very similar things
     * they were pulled into this class reduce code duplication.
     */
    private class ArrowKeyAction extends AbstractAction {

        private final int key;

        /**
         * Constructs a ArrowKeyAction from the given key.
         *
         * @param key The key that the action fires in response to - it must be an arrow key.
         */
        private ArrowKeyAction(int key) {
            this.key = key;
            if (key != KeyEvent.VK_LEFT && key != KeyEvent.VK_RIGHT && key != KeyEvent.VK_UP && key != KeyEvent.VK_DOWN)
                throw new IllegalArgumentException("'key' passed to ArrowKeyAction was not an arrow key.");
        }

        /**
         * Fires in response to a key press event - moving any selected Nodes/Edges, or moving the whole panel view if
         * none are selected.
         *
         * @param e The ActionEvent arguments.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            double deltaX = 0.0;
            double deltaY = 0.0;
            switch (key) {
                case KeyEvent.VK_LEFT:
                    deltaX = -0.05 * panel.getWidth();
                    break;
                case KeyEvent.VK_RIGHT:
                    deltaX = +0.05 * panel.getWidth();
                    break;
                case KeyEvent.VK_UP:
                    deltaY = -0.05 * panel.getHeight();
                    break;
                case KeyEvent.VK_DOWN:
                    deltaY = +0.05 * panel.getHeight();
                    break;
            }

            if (graph.numSelectedNodes() == 0 && graph.numSelectedEdges() == 0)
                panel.translate(-deltaX, -deltaY);
            else {
                deltaX /= panel.getScale();
                deltaY /= panel.getScale();
                if (graph.getHoveredEdge() == null || graph.getHoveredNode() != null)
                    graph.moveSelectedNodesBy(deltaX, deltaY);
                else
                    graph.moveSelectedEdgesBy(deltaX, deltaY);
            }
        }
    }

}